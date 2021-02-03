/**
 * 
 */
package de.kjEngine.graphics.shader.parser.spirv;

import de.kjEngine.graphics.shader.parser.spirv.Instruction.Category;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ID;

/**
 * @author konst
 *
 */
public class DeadCodeOptimization implements Optimization {

	public static final DeadCodeOptimization INSTANCE = new DeadCodeOptimization();

	public DeadCodeOptimization() {
	}

	@Override
	public int run(Spirv code) {
		int count = 0;
		
		for (int instructionIndex = 0; instructionIndex < code.instructions.length(); instructionIndex++) {
			Instruction instruction = code.instructions.get(instructionIndex);
			if (!instruction.type.hasResultId || instruction.type.category != Category.Decleration) {
				continue;
			}

			boolean hasDependencies = false;
			outer_loop: for (Instruction other : code.instructions) {
				if (other == instruction) {
					continue;
				}
				if (other.id == instruction.resultId) {
					hasDependencies = true;
					break outer_loop;
				}
				for (int i = 0; i < other.operands.length(); i++) {
					Operand<?> op = other.operands.get(i);
					if (op instanceof ID && ((ID) op).value == instruction.resultId) {
						hasDependencies = true;
						break outer_loop;
					}
				}
			}
			if (hasDependencies) {
				continue;
			}

			code.instructions.remove(instruction);
			count++;
		}
		return count;
	}
}
