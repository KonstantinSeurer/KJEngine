/**
 * 
 */
package de.kjEngine.graphics.shader.parser.spirv;

import de.kjEngine.graphics.shader.parser.spirv.Instruction.Type;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ExtendedType;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ID;

/**
 * @author konst
 *
 */
public class FmaOptimization implements Optimization {

	public static final FmaOptimization INSTANCE = new FmaOptimization();

	public FmaOptimization() {
	}
	
	@Override
	public int run(Spirv code) {
		Instruction glsl450Import = null;
		for (Instruction instruction : code.instructions) {
			if (instruction.is(Type.ExtInstImport)) {
				glsl450Import = instruction;
				break;
			}
		}
		
		int count = 0;
		for (int instructionIndex = 0; instructionIndex < code.instructions.length(); instructionIndex++) {
			Instruction instruction = code.instructions.get(instructionIndex);
			if (instruction.type == Type.FAdd) {
				Instruction argA = instruction.getInstruction(0);
				Instruction argB = instruction.getInstruction(1);
				Instruction mulInstruction = null;
				Instruction otherInstruction = null;
				if (argA.is(Type.FMul)) {
					mulInstruction = argA;
					otherInstruction = argB;
				} else if (argB.is(Type.FMul)) {
					mulInstruction = argB;
					otherInstruction = argA;
				}
				if (mulInstruction == null) {
					continue;
				}
				boolean hasOtherDependencies = false;
				outer_loop: for (Instruction other : code.instructions) {
					if (other == instruction) {
						continue;
					}
					for (int i = 0; i < other.operands.length(); i++) {
						Operand<?> op = other.operands.get(i);
						if (op instanceof ID && ((ID) op).value == mulInstruction.resultId) {
							hasOtherDependencies = true;
							break outer_loop;
						}
					}
				}
				if (hasOtherDependencies) {
					continue;
				}
				
				int a = mulInstruction.operandWords[0];
				int b = mulInstruction.operandWords[1];
				int c = otherInstruction.resultId;
				
				Instruction fma = new Instruction(code, Type.ExtInst, instruction.id, instruction.resultId, glsl450Import.resultId, ExtendedType.Fma.opCode, a, b, c);
				
				code.instructions.set(instructionIndex, fma);
				code.instructions.remove(mulInstruction);
				
				count++;
			}
		}
		return count;
	}
}
