/**
 * 
 */
package de.kjEngine.graphics.shader.parser.spirv;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

import de.kjEngine.graphics.shader.parser.spirv.Instruction.Type;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class Spirv {

	public final int magicNumber;
	public final int version;
	public final int majorVersion, minorVersion;
	public final int generatorMagicNumber;
	public final int bound;
	public final Array<Instruction> instructions = new Array<>();

	public Spirv(ByteBuffer buffer) {
		buffer.position(0);
		magicNumber = buffer.getInt();
		version = buffer.getInt();
		majorVersion = (version & 0x00FF0000) >> 16;
		minorVersion = (version & 0x0000FF00) >> 16;
		generatorMagicNumber = buffer.getInt();
		bound = buffer.getInt();
		buffer.getInt(); // reserved

		while (buffer.position() < buffer.limit()) {
			instructions.add(new Instruction(this, buffer));
		}

		buffer.position(0);
	}

	public ByteBuffer get() {
		Array<Integer> array = new Array<>();
		array.add(magicNumber);
		array.add(version);
		array.add(generatorMagicNumber);
		array.add(bound);
		array.add(0); // reserved
		for (int i = 0; i < instructions.length(); i++) {
			instructions.get(i).get(array);
		}

		ByteBuffer buffer = MemoryUtil.memAlloc(array.length() * 4);
		for (int i = 0; i < array.length(); i++) {
			buffer.putInt(array.get(i));
		}
		buffer.position(0);
		return buffer;
	}

	public Map<Optimization, Integer> optimize(Array<Optimization> optiizations) {
		Map<Optimization, Integer> statistics = new HashMap<>();
		int count;
		do {
			count = 0;
			for (Optimization o : optiizations) {
				if (!statistics.containsKey(o)) {
					statistics.put(o, 0);
				}
				int currentCount = o.run(this);
				if (currentCount > 0) {
					statistics.put(o, statistics.get(o) + currentCount);
				}
				count += currentCount;
			}
		} while (count > 0);
		return statistics;
	}

	private static class Connection {
		int src, dst;
		int level;
	}

	private Connection createConnection(int src, int dstId) {
		Connection c = new Connection();
		c.src = src;
		for (int i = 0; i < instructions.length(); i++) {
			Instruction label = instructions.get(i);
			if (label.type != Type.Label || label.resultId != dstId) {
				continue;
			}
			c.dst = i;
		}
		if (c.src > c.dst) {
			int temp = c.src;
			c.src = c.dst;
			c.dst = temp;
		}
		return c;
	}

	@Override
	public String toString() {
		Array<StringBuilder> lines = new Array<>();
		int indentation = 0;
		for (int i = 0; i < instructions.length(); i++) {
			Instruction op = instructions.get(i);
			switch (op.type) {
			case Branch:
			case BranchConditional:
			case FunctionEnd:
			case Return:
			case ReturnValue:
				indentation--;
				break;
			default:
			}
			lines.add(new StringBuilder(op.toString(indentation)));
			switch (op.type) {
			case Label:
			case Function:
				indentation++;
				break;
			default:
			}
		}

		Array<Connection> connections = new Array<>();
		for (int i = 0; i < instructions.length(); i++) {
			Instruction op = instructions.get(i);
			if (op.type == Type.Branch) {
				connections.add(createConnection(i, op.operandWords[0]));
			} else if (op.type == Type.BranchConditional) {
				connections.add(createConnection(i, op.operandWords[1]));
				connections.add(createConnection(i, op.operandWords[2]));
			}
		}

		int totalMaxLevel = 0;
		for (int i = 0; i < connections.length(); i++) {
			Connection c = connections.get(i);
			for (int pos = c.src; pos <= c.dst; pos++) {
				int level = 0;
				for (int j = 0; j < connections.length(); j++) {
					if (i == j) {
						continue;
					}
					Connection other = connections.get(j);
					if (pos >= other.src && pos <= other.dst) {
						level = Math.max(level, other.level + 1);
					}
				}
				c.level = Math.max(c.level, level);
				totalMaxLevel = Math.max(totalMaxLevel, level);
			}
		}

		int maxLineLength = 0;
		for (int lineIndex = 0; lineIndex < lines.length(); lineIndex++) {
			StringBuilder line = lines.get(lineIndex);
			maxLineLength = Math.max(maxLineLength, line.length());
		}

		for (int lineIndex = 0; lineIndex < lines.length(); lineIndex++) {
			StringBuilder line = lines.get(lineIndex);
			boolean isSrcOrDst = false;
			int maxLevel = 0;
			for (int i = 0; i < connections.length(); i++) {
				Connection c = connections.get(i);
				if (c.src == lineIndex || c.dst == lineIndex) {
					isSrcOrDst = true;
					maxLevel = Math.max(maxLevel, c.level);
				}
			}
			int requiredCaracters = 4 + maxLineLength - line.length();

			int startLevel = maxLevel;

			if (isSrcOrDst) {
				for (int i = 0; i < requiredCaracters; i++) {
					line.append("-");
				}
				for (int level = 0; level <= maxLevel; level++) {
					if (instructions.get(lineIndex).resultId == 188) {
						System.out.println();
					}
					boolean plus = false;
					for (int i = 0; i < connections.length(); i++) {
						Connection c = connections.get(i);
						if (c.level != level) {
							continue;
						}
						if (c.src == lineIndex || c.dst == lineIndex) {
							plus = true;
							break;
						}
					}
					if (plus) {
						line.append("---+");
					} else {
						line.append("----");
					}
				}
				startLevel++;
				line.append("   ");
			} else {
				for (int i = 0; i < requiredCaracters + 3; i++) {
					line.append(" ");
				}
			}
			for (int level = startLevel; level <= totalMaxLevel; level++) {
				boolean connection = false;
				for (int i = 0; i < connections.length(); i++) {
					Connection c = connections.get(i);
					if (c.src > lineIndex || c.dst < lineIndex) {
						continue;
					}
					if (c.level == level) {
						connection = true;
						break;
					}
				}
				if (connection) {
					line.append("|   ");
				} else {
					line.append("    ");
				}
			}
		}

		lines.add(0, new StringBuilder().append("bound = ").append(bound));
		lines.add(0, new StringBuilder().append("generatorMagicNumber = ").append(generatorMagicNumber));
		lines.add(0, new StringBuilder().append("version = ").append(majorVersion).append('.').append(minorVersion));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.length(); i++) {
			sb.append(lines.get(i));
			sb.append("\n");
		}
		return sb.toString();
	}

	public Instruction resolveId(int id) {
		return instructions.get((op) -> {
			return op.resultId == id;
		});
	}
}
