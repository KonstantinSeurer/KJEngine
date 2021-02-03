/**
 * 
 */
package de.kjEngine.graphics.shader.parser.spirv;

import de.kjEngine.graphics.shader.parser.spirv.Instruction.Category;
import de.kjEngine.graphics.shader.parser.spirv.Instruction.Type;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ID;

/**
 * @author konst
 *
 */
public class BranchThreadingOptimization implements Optimization {

	public static final BranchThreadingOptimization INSTANCE = new BranchThreadingOptimization();

	public BranchThreadingOptimization() {
	}

	@Override
	public int run(Spirv code) {
		int count = 0;
		for (int instructionIndex = 0; instructionIndex < code.instructions.length(); instructionIndex++) {
			Instruction branch = code.instructions.get(instructionIndex);
			if (branch.is(Type.Branch)) {
				count += optimizeBranch(code, instructionIndex, 0);
			} else if (branch.is(Type.BranchConditional)) {
				count += optimizeBranch(code, instructionIndex, 1);
				count += optimizeBranch(code, instructionIndex, 2);
			}
		}
		return count;
	}

	private int optimizeBranch(Spirv code, int instructionIndex, int operandIndex) {
		Instruction instruction = code.instructions.get(instructionIndex);
		int id = instruction.operandWords[operandIndex];
		int nextId = optimizeBranch(instruction.parent, id);
		if (id != nextId) {
			int labelId = 0;
			for (int i = instructionIndex - 1; i >= 0; i--) {
				Instruction label = code.instructions.get(i);
				if (label.is(Type.Label)) {
					labelId = label.resultId;
					break;
				}
			}
			// change the phi functions that reference the replaced branch if this is the
			// only path leading to the phi function through the replaced branch
			// count the number of other branches that end up in this label
			int idRefCount = 0;
			for (int i = 0; i < code.instructions.length(); i++) {
				if (i == instructionIndex) {
					continue;
				}
				Instruction branch = code.instructions.get(i);
				if (branch.type.category == Category.Structure) {
					for (int j = 0; j < branch.operandWords.length; j++) {
						if (branch.operandWords[j] == id) {
							idRefCount++;
						}
					}
				}
			}
			if (idRefCount > 0) {
				return 0;
			}
			// update phi functions
			for (int i = 0; i < code.instructions.length(); i++) {
				Instruction phi = code.instructions.get(i);
				if (!phi.is(Type.Phi)) {
					continue;
				}
				for (int j = 0; j < phi.operandWords.length; j++) {
					if (phi.operandWords[j] == id) {
						phi.operandWords[j] = nextId;
						phi.operands.set(j, new ID(new int[] { nextId }, 0));
					}
				}
			}
			System.out.println(id + " " + nextId + " " + labelId + " " + idRefCount);
			instruction.operandWords[operandIndex] = nextId;
			instruction.operands.set(operandIndex, new ID(new int[] { nextId }, 0));
			return 1;
		}
		return 0;
	}

	private int optimizeBranch(Spirv code, int targetId) {
		Instruction target = code.resolveId(targetId);
		int targetIndex = code.instructions.indexOf(target);
		Instruction nextInstruction = code.instructions.get(targetIndex + 1);
		if (nextInstruction.is(Type.Branch)) {
			return nextInstruction.operandWords[0];
		}
		return targetId;
	}
}
