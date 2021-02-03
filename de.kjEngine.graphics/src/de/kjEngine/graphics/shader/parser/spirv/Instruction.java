/**
 * 
 */
package de.kjEngine.graphics.shader.parser.spirv;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import de.kjEngine.graphics.shader.parser.spirv.Operand.AccessQualifierLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.AddressingModelLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.CapabilityLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.DecorationLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.DimLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ExecutionModeLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ExecutionModelLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ExtendedTypeLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.FunctionControlLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ID;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ImageFormatLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.ImageLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.InstructionTypeLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.LoopControlLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.MemoryLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.MemoryModelLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.SamplerAddressingModeLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.SamplerFilterModeLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.SelectionControlLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.SignedLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.StorageClassLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.StringLiteral;
import de.kjEngine.graphics.shader.parser.spirv.Operand.UnsignedLiteral;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class Instruction {

	public static interface OperandProvider {

		public void get(int[] src, Array<Operand<?>> dst);
	}

	private static final OperandProvider NONE = (src, dst) -> {
	};

	private static final OperandProvider ONE_ID = (src, dst) -> {
		dst.add(new ID(src, 0));
	};

	private static final OperandProvider TWO_IDS = (src, dst) -> {
		dst.add(new ID(src, 0));
		dst.add(new ID(src, 1));
	};

	private static final OperandProvider THREE_IDS = (src, dst) -> {
		dst.add(new ID(src, 0));
		dst.add(new ID(src, 1));
		dst.add(new ID(src, 2));
	};

	private static final OperandProvider FOUR_IDS = (src, dst) -> {
		dst.add(new ID(src, 0));
		dst.add(new ID(src, 1));
		dst.add(new ID(src, 2));
		dst.add(new ID(src, 3));
	};

	private static final OperandProvider SIX_IDS = (src, dst) -> {
		dst.add(new ID(src, 0));
		dst.add(new ID(src, 1));
		dst.add(new ID(src, 2));
		dst.add(new ID(src, 3));
		dst.add(new ID(src, 4));
		dst.add(new ID(src, 5));
	};

	private static final OperandProvider ONE_STRING = (src, dst) -> {
		dst.add(new StringLiteral(src, 0));
	};

	private static final OperandProvider ID_ID_IMAGE = (src, dst) -> {
		dst.add(new ID(src, 0));
		dst.add(new ID(src, 1));
		if (src.length > 2) {
			dst.add(new ImageLiteral(src, 2));
		}
		for (int i = 3; i < src.length; i++) {
			dst.add(new ID(src, i));
		}
	};

	private static final OperandProvider ID_ID_ID_IMAGE = (src, dst) -> {
		dst.add(new ID(src, 0));
		dst.add(new ID(src, 1));
		dst.add(new ID(src, 2));
		if (src.length > 3) {
			dst.add(new ImageLiteral(src, 3));
		}
		for (int i = 4; i < src.length; i++) {
			dst.add(new ID(src, i));
		}
	};

	private static final OperandProvider ID_ID_IMAGE_ID = (src, dst) -> {
		dst.add(new ID(src, 0));
		dst.add(new ID(src, 1));
		dst.add(new ImageLiteral(src, 2));
		dst.add(new ID(src, 3));
		for (int i = 4; i < src.length; i++) {
			dst.add(new ID(src, i));
		}

	};

	private static final OperandProvider ID_ID_ID_IMAGE_ID = (src, dst) -> {
		dst.add(new ID(src, 0));
		dst.add(new ID(src, 1));
		dst.add(new ID(src, 2));
		dst.add(new ImageLiteral(src, 3));
		dst.add(new ID(src, 4));
		for (int i = 5; i < src.length; i++) {
			dst.add(new ID(src, i));
		}
	};
	
	public static enum Category {
		Information, DebugInformation, Annotation, Decleration, Structure, SideEffect;
	}

	public static enum Type {
		// misc
		Nop(0, false, false, NONE, Category.DebugInformation),
		Undef(1, true, true, NONE, Category.Decleration),
		SizeOf(321, true, true, ONE_ID, Category.Decleration),
		// debug
		SourceContinued(2, false, false, ONE_STRING, Category.DebugInformation),
		Source(3, false, false, (src, dst) -> {
			dst.add(new UnsignedLiteral(src, 0));
			dst.add(new UnsignedLiteral(src, 1));
			if (src.length > 2) {
				StringLiteral file = (StringLiteral) dst.add(new StringLiteral(src, 2));
				if (src.length > 2 + file.length) {
					dst.add(new StringLiteral(src, 2 + file.length));
				}
			}
		}, Category.DebugInformation),
		SourceExtension(4, false, false, ONE_STRING, Category.DebugInformation),
		Name(5, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new StringLiteral(src, 1));
		}, Category.DebugInformation),
		MemberName(6, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			StringLiteral member = (StringLiteral) dst.add(new StringLiteral(src, 1));
			dst.add(new StringLiteral(src, 1 + member.length));
		}, Category.DebugInformation),
		String(7, false, true, ONE_STRING, Category.DebugInformation),
		Line(8, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new UnsignedLiteral(src, 1));
			dst.add(new UnsignedLiteral(src, 2));
		}, Category.DebugInformation),
		NoLine(317, false, false, NONE, Category.DebugInformation),
		ModuleProcessed(330, false, false, ONE_STRING, Category.DebugInformation),
		// annotation
		Decorate(71, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new DecorationLiteral(src, 1));
			// TODO: implement
		}, Category.Annotation),
		MemberDecorate(72, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new SignedLiteral(src, 1));
			dst.add(new DecorationLiteral(src, 2));
			// TODO: implement
		}, Category.Annotation),
		DecorationGroup(73, false, true, NONE, Category.Annotation),
		GroupDecorate(74, false, false, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Annotation),
		GroupMemberDecorate(75, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			for (int i = 1; i < src.length; i += 2) {
				dst.add(new ID(src, i));
				dst.add(new SignedLiteral(src, i + 1));
			}
		}, Category.Annotation),
		DecorateId(332, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new DecorationLiteral(src, 1));
			// TODO: implement
		}, Category.Annotation),
		DecorateString(5632, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new DecorationLiteral(src, 1));
			int i = 2;
			while (i < src.length) {
				i += dst.add(new StringLiteral(src, i)).length;
			}
		}, Category.Annotation),
		MemberDecorateString(5633, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new UnsignedLiteral(src, 1));
			dst.add(new DecorationLiteral(src, 2));
			// TODO: implement
		}, Category.Annotation),
		// extension
		Extension(10, false, false, ONE_STRING, Category.Information),
		ExtInstImport(11, false, true, ONE_STRING, Category.Information),
		ExtInst(12, true, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ExtendedTypeLiteral(src, 1));
			for (int i = 2; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		// mode setting
		MemoryModel(14, false, false, (src, dst) -> {
			dst.add(new AddressingModelLiteral(src, 0));
			dst.add(new MemoryModelLiteral(src, 1));
		}, Category.Information),
		EntryPoint(15, false, false, (src, dst) -> {
			dst.add(new ExecutionModelLiteral(src, 0));
			dst.add(new ID(src, 1));
			int offset = dst.add(new StringLiteral(src, 2)).length + 2;
			for (int i = offset; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Information),
		ExecutionMode(16, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ExecutionModeLiteral(src, 1));
		}, Category.Information),
		Capability(17, false, false, (src, dst) -> {
			dst.add(new CapabilityLiteral(src, 0));
		}, Category.Information),
		ExecutionModeId(331, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ExecutionModelLiteral(src, 1));
		}, Category.Information),
		// type declaration
		TypeVoid(19, false, true, NONE, Category.Decleration),
		TypeBool(20, false, true, NONE, Category.Decleration),
		TypeInt(21, false, true, (src, dst) -> {
			dst.add(new UnsignedLiteral(src, 0));
			dst.add(new UnsignedLiteral(src, 1));
		}, Category.Decleration),
		TypeFloat(22, false, true, (src, dst) -> {
			dst.add(new UnsignedLiteral(src, 0));
		}, Category.Decleration),
		TypeVector(23, false, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new UnsignedLiteral(src, 1));
		}, Category.Decleration),
		TypeMatrix(24, false, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new UnsignedLiteral(src, 1));
		}, Category.Decleration),
		TypeImage(25, false, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new DimLiteral(src, 1));
			dst.add(new UnsignedLiteral(src, 2));
			dst.add(new UnsignedLiteral(src, 3));
			dst.add(new UnsignedLiteral(src, 4));
			dst.add(new UnsignedLiteral(src, 5));
			dst.add(new ImageFormatLiteral(src, 6));
			if (src.length == 8) {
				dst.add(new AccessQualifierLiteral(src, 7));
			}
		}, Category.Decleration),
		TypeSampler(26, false, true, NONE, Category.Decleration),
		TypeSampledImage(27, false, true, ONE_ID, Category.Decleration),
		TypeArray(28, false, true, TWO_IDS, Category.Decleration),
		TypeRuntimeArray(29, false, true, ONE_ID, Category.Decleration),
		TypeStruct(30, false, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		TypeOpaque(31, false, true, ONE_STRING, Category.Decleration),
		TypePointer(32, false, true, (src, dst) -> {
			dst.add(new StorageClassLiteral(src, 0));
			dst.add(new ID(src, 1));
		}, Category.Decleration),
		TypeFunction(33, false, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		TypeForwardPointer(39, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new StorageClassLiteral(src, 1));
		}, Category.Decleration),
		TypeNamedBarrier(327, false, true, NONE, Category.Decleration),
		// constant
		ConstantTrue(41, true, true, NONE, Category.Decleration),
		ConstantFalse(42, true, true, NONE, Category.Decleration),
		Constant(43, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new SignedLiteral(src, i));
			}
		}, Category.Decleration),
		ConstantComposite(44, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		ConstantSampler(45, true, true, (src, dst) -> {
			dst.add(new SamplerAddressingModeLiteral(src, 0));
			dst.add(new SignedLiteral(src, 1));
			dst.add(new SamplerFilterModeLiteral(src, 2));
		}, Category.Decleration),
		ConstantNull(46, true, true, NONE, Category.Decleration),
		SpecConstantTrue(48, true, true, NONE, Category.Decleration),
		SpecConstantFalse(49, true, true, NONE, Category.Decleration),
		SpecConstant(50, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new SignedLiteral(src, i));
			}
		}, Category.Decleration),
		SpecConstantComposite(51, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		SpecConstantOp(52, true, true, (src, dst) -> {
			dst.add(new InstructionTypeLiteral(src, 0));
			for (int i = 1; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		// memory
		Variable(59, true, true, (src, dst) -> {
			dst.add(new StorageClassLiteral(src, 0));
			if (src.length == 2) {
				dst.add(new ID(src, 1));
			}
		}, Category.Decleration),
		ImageTexelPointer(60, true, true, THREE_IDS, Category.Decleration),
		Load(61, true, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			if (src.length == 2) {
				dst.add(new MemoryLiteral(src, 1));
			}
		}, Category.Decleration),
		Store(62, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ID(src, 1));
			if (src.length == 3) {
				dst.add(new MemoryLiteral(src, 2));
			}
		}, Category.SideEffect),
		CopyMemory(63, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ID(src, 1));
			for (int i = 2; i < src.length; i++) {
				dst.add(new MemoryLiteral(src, i));
			}
		}, Category.SideEffect),
		CopyMemorySized(64, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ID(src, 1));
			dst.add(new ID(src, 2));
			for (int i = 3; i < src.length; i++) {
				dst.add(new MemoryLiteral(src, i));
			}
		}, Category.SideEffect),
		AccessChain(65, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		InBoundsAccessChain(66, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		PtrAccessChain(67, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		ArrayLength(68, true, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new UnsignedLiteral(src, 1));
		}, Category.Decleration),
		GenericPtrMemSemantics(69, true, true, ONE_ID, Category.Decleration),
		InBoundsPtrAccessChain(70, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		PtrEqual(401, true, true, TWO_IDS, Category.Decleration),
		PtrNotEqual(402, true, true, TWO_IDS, Category.Decleration),
		PtrDiff(403, true, true, TWO_IDS, Category.Decleration),
		// function
		Function(54, true, true, (src, dst) -> {
			dst.add(new FunctionControlLiteral(src, 0));
			dst.add(new ID(src, 1));
		}, Category.Decleration),
		FunctionParameter(55, true, true, NONE, Category.Decleration),
		FunctionEnd(56, false, false, NONE, Category.Structure),
		FunctionCall(57, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		// image
		SampledImage(86, true, true, TWO_IDS, Category.Decleration),
		ImageSampleImplicitLod(87, true, true, ID_ID_IMAGE, Category.Decleration),
		ImageSampleExplicitLod(88, true, true, ID_ID_IMAGE_ID, Category.Decleration),
		ImageSampleDrefImplicitLod(89, true, true, ID_ID_ID_IMAGE, Category.Decleration),
		ImageSampleDrefExplicitLod(90, true, true, ID_ID_ID_IMAGE_ID, Category.Decleration),
		ImageSampleProjImplicitLod(91, true, true, ID_ID_IMAGE, Category.Decleration),
		ImageSampleProjExplicitLod(92, true, true, ID_ID_IMAGE_ID, Category.Decleration),
		ImageSampleProjDrefImplicitLod(93, true, true, ID_ID_ID_IMAGE, Category.Decleration),
		ImageSampleProjDrefExplicitLod(94, true, true, ID_ID_ID_IMAGE_ID, Category.Decleration),
		ImageFetch(95, true, true, ID_ID_IMAGE, Category.Decleration),
		ImageGather(96, true, true, ID_ID_ID_IMAGE, Category.Decleration),
		ImageDrefGather(97, true, true, ID_ID_ID_IMAGE, Category.Decleration),
		ImageRead(98, true, true, ID_ID_IMAGE, Category.Decleration),
		ImageWrite(99, false, false, ID_ID_ID_IMAGE, Category.SideEffect),
		Image(100, true, true, ONE_ID, Category.Decleration),
		ImageQueryFormat(101, true, true, ONE_ID, Category.Decleration),
		ImageQueryOrder(102, true, true, ONE_ID, Category.Decleration),
		ImageQuerySizeLod(103, true, true, TWO_IDS, Category.Decleration),
		ImageQuerySize(104, true, true, ONE_ID, Category.Decleration),
		ImageQueryLod(105, true, true, TWO_IDS, Category.Decleration),
		ImageQueryLevels(106, true, true, ONE_ID, Category.Decleration),
		ImageQuerySamples(107, true, true, ONE_ID, Category.Decleration),
		ImageSparseSampleImplicitLod(305, true, true, ID_ID_IMAGE, Category.Decleration),
		ImageSparseSampleExplicitLod(306, true, true, ID_ID_IMAGE_ID, Category.Decleration),
		ImageSparseSampleDrefImplicitLod(307, true, true, ID_ID_ID_IMAGE, Category.Decleration),
		ImageSparseSampleDrefExplicitLod(308, true, true, ID_ID_ID_IMAGE_ID, Category.Decleration),
		ImageSparseFetch(313, true, true, ID_ID_IMAGE, Category.Decleration),
		ImageSparseGather(314, true, true, ID_ID_ID_IMAGE, Category.Decleration),
		ImageSparseDrefGather(315, true, true, ID_ID_ID_IMAGE, Category.Decleration),
		ImageSparseTexelsResident(316, true, true, ONE_ID, Category.Decleration),
		ImageSparseRead(320, true, true, ID_ID_IMAGE, Category.Decleration),
		// converson
		ConvertFToU(109, true, true, ONE_ID, Category.Decleration),
		ConvertFToS(110, true, true, ONE_ID, Category.Decleration),
		ConvertSToF(111, true, true, ONE_ID, Category.Decleration),
		ConvertUToF(112, true, true, ONE_ID, Category.Decleration),
		UConvert(113, true, true, ONE_ID, Category.Decleration),
		SConvert(114, true, true, ONE_ID, Category.Decleration),
		FConvert(115, true, true, ONE_ID, Category.Decleration),
		QuantizeToF16(116, true, true, ONE_ID, Category.Decleration),
		ConvertPtrToU(117, true, true, ONE_ID, Category.Decleration),
		SatConvertSToU(118, true, true, ONE_ID, Category.Decleration),
		SatConvertUToS(119, true, true, ONE_ID, Category.Decleration),
		ConvertUToPtr(120, true, true, ONE_ID, Category.Decleration),
		PtrCastToGeneric(121, true, true, ONE_ID, Category.Decleration),
		GenericCastToPtr(122, true, true, ONE_ID, Category.Decleration),
		GenericCastToPtrExplicit(123, true, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new StorageClassLiteral(src, 1));
		}, Category.Decleration),
		Bitcast(124, true, true, ONE_ID, Category.Decleration),
		// composite
		VectorExtractDynamic(77, true, true, TWO_IDS, Category.Decleration),
		VectorInsertDynamic(78, true, true, THREE_IDS, Category.Decleration),
		VectorShuffle(79, true, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ID(src, 1));
			for (int i = 2; i < src.length; i++) {
				dst.add(new UnsignedLiteral(src, i));
			}
		}, Category.Decleration),
		CompositeConstruct(80, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		CompositeExtract(81, true, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			for (int i = 1; i < src.length; i++) {
				dst.add(new UnsignedLiteral(src, i));
			}
		}, Category.Decleration),
		CompositeInsert(82, true, true, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ID(src, 1));
			for (int i = 2; i < src.length; i++) {
				dst.add(new UnsignedLiteral(src, i));
			}
		}, Category.Decleration),
		CopyObject(83, true, true, ONE_ID, Category.Decleration),
		Transpose(84, true, true, ONE_ID, Category.Decleration),
		CopyLogical(400, true, true, ONE_ID, Category.Decleration),
		// arithmetic
		SNegate(126, true, true, ONE_ID, Category.Decleration),
		FNegate(127, true, true, ONE_ID, Category.Decleration),
		IAdd(128, true, true, TWO_IDS, Category.Decleration),
		FAdd(129, true, true, TWO_IDS, Category.Decleration),
		ISub(130, true, true, TWO_IDS, Category.Decleration),
		FSub(131, true, true, TWO_IDS, Category.Decleration),
		IMul(132, true, true, TWO_IDS, Category.Decleration),
		FMul(133, true, true, TWO_IDS, Category.Decleration),
		UDiv(134, true, true, TWO_IDS, Category.Decleration),
		SDiv(135, true, true, TWO_IDS, Category.Decleration),
		FDiv(136, true, true, TWO_IDS, Category.Decleration),
		UMod(137, true, true, TWO_IDS, Category.Decleration),
		SRem(138, true, true, TWO_IDS, Category.Decleration),
		SMod(139, true, true, TWO_IDS, Category.Decleration),
		FRem(140, true, true, TWO_IDS, Category.Decleration),
		FMod(141, true, true, TWO_IDS, Category.Decleration),
		VectorTimesScalar(142, true, true, TWO_IDS, Category.Decleration),
		MatrixTimesScalar(143, true, true, TWO_IDS, Category.Decleration),
		VectorTimesMatrix(144, true, true, TWO_IDS, Category.Decleration),
		MatrixTimesVector(145, true, true, TWO_IDS, Category.Decleration),
		MatrixTimesMatrix(146, true, true, TWO_IDS, Category.Decleration),
		OuterProduct(147, true, true, TWO_IDS, Category.Decleration),
		Dot(148, true, true, TWO_IDS, Category.Decleration),
		IAddCarry(149, true, true, TWO_IDS, Category.Decleration),
		ISubBorrow(150, true, true, TWO_IDS, Category.Decleration),
		UMulExtended(151, true, true, TWO_IDS, Category.Decleration),
		SMulExtended(152, true, true, TWO_IDS, Category.Decleration),
		// bit
		ShiftRightLogical(194, true, true, TWO_IDS, Category.Decleration),
		ShiftRightArithmetic(195, true, true, TWO_IDS, Category.Decleration),
		ShiftLeftLogical(196, true, true, TWO_IDS, Category.Decleration),
		BitwiseOr(197, true, true, TWO_IDS, Category.Decleration),
		BitwiseXor(198, true, true, TWO_IDS, Category.Decleration),
		BitwiseAnd(199, true, true, TWO_IDS, Category.Decleration),
		Not(200, true, true, ONE_ID, Category.Decleration),
		BitFieldInsert(201, true, true, FOUR_IDS, Category.Decleration),
		BitFieldSExtract(202, true, true, THREE_IDS, Category.Decleration),
		BitFieldUExtract(203, true, true, THREE_IDS, Category.Decleration),
		BitReverse(204, true, true, ONE_ID, Category.Decleration),
		BitCount(205, true, true, ONE_ID, Category.Decleration),
		// logical
		Any(154, true, true, ONE_ID, Category.Decleration),
		All(155, true, true, ONE_ID, Category.Decleration),
		IsNan(156, true, true, ONE_ID, Category.Decleration),
		IsInf(157, true, true, ONE_ID, Category.Decleration),
		IsFinite(158, true, true, ONE_ID, Category.Decleration),
		IsNormal(159, true, true, ONE_ID, Category.Decleration),
		SignBitSet(160, true, true, ONE_ID, Category.Decleration),
		LessOrGreater(161, true, true, TWO_IDS, Category.Decleration),
		Ordered(162, true, true, TWO_IDS, Category.Decleration),
		Unordered(163, true, true, TWO_IDS, Category.Decleration),
		LogicalEqual(164, true, true, TWO_IDS, Category.Decleration),
		LogicalNotEqual(165, true, true, TWO_IDS, Category.Decleration),
		LogicalOr(166, true, true, TWO_IDS, Category.Decleration),
		LogicalAnd(167, true, true, TWO_IDS, Category.Decleration),
		LogicalNot(168, true, true, ONE_ID, Category.Decleration),
		Select(169, true, true, THREE_IDS, Category.Decleration),
		IEqual(170, true, true, TWO_IDS, Category.Decleration),
		INotEqual(171, true, true, TWO_IDS, Category.Decleration),
		UGreaterThan(172, true, true, TWO_IDS, Category.Decleration),
		SGreaterThan(173, true, true, TWO_IDS, Category.Decleration),
		UGreaterThanEqual(174, true, true, TWO_IDS, Category.Decleration),
		SGreaterThanEqual(175, true, true, TWO_IDS, Category.Decleration),
		ULessThan(176, true, true, TWO_IDS, Category.Decleration),
		SLessThan(177, true, true, TWO_IDS, Category.Decleration),
		ULessThanEqual(178, true, true, TWO_IDS, Category.Decleration),
		SLessThanEqual(179, true, true, TWO_IDS, Category.Decleration),
		FOrdEqual(180, true, true, TWO_IDS, Category.Decleration),
		FUnordEqual(181, true, true, TWO_IDS, Category.Decleration),
		FOrdNotEqual(182, true, true, TWO_IDS, Category.Decleration),
		FUnordNotEqual(183, true, true, TWO_IDS, Category.Decleration),
		FOrdLessThan(184, true, true, TWO_IDS, Category.Decleration),
		FUnordLessThan(185, true, true, TWO_IDS, Category.Decleration),
		FOrdGreaterThan(186, true, true, TWO_IDS, Category.Decleration),
		FUnordGreaterThan(187, true, true, TWO_IDS, Category.Decleration),
		FOrdLessThanEqual(188, true, true, TWO_IDS, Category.Decleration),
		FUnordLessThanEqual(189, true, true, TWO_IDS, Category.Decleration),
		FOrdGreaterThanEqual(190, true, true, TWO_IDS, Category.Decleration),
		FUnordGreaterThanEqual(191, true, true, TWO_IDS, Category.Decleration),
		// derivative
		DPdx(207, true, true, ONE_ID, Category.Decleration),
		DPdy(208, true, true, ONE_ID, Category.Decleration),
		Fwidth(209, true, true, ONE_ID, Category.Decleration),
		DPdxFine(210, true, true, ONE_ID, Category.Decleration),
		DPdyFine(211, true, true, ONE_ID, Category.Decleration),
		FwidthFine(212, true, true, ONE_ID, Category.Decleration),
		DPdxCoarse(213, true, true, ONE_ID, Category.Decleration),
		DPdyCoarse(214, true, true, ONE_ID, Category.Decleration),
		FwidthCoarse(215, true, true, ONE_ID, Category.Decleration),
		// control flow
		Phi(245, true, true, (src, dst) -> {
			for (int i = 0; i < src.length; i++) {
				dst.add(new ID(src, i));
			}
		}, Category.Decleration),
		LoopMerge(246, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ID(src, 1));
			dst.add(new LoopControlLiteral(src, 2));
			for (int i = 3; i < src.length; i++) {
				dst.add(new SignedLiteral(src, i));
			}
		}, Category.Structure),
		SelectionMerge(247, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new SelectionControlLiteral(src, 1));
		}, Category.Structure),
		Label(248, false, true, NONE, Category.Structure),
		Branch(249, false, false, ONE_ID, Category.Structure),
		BranchConditional(250, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ID(src, 1));
			dst.add(new ID(src, 2));
			for (int i = 3; i < src.length; i++) {
				dst.add(new SignedLiteral(src, i));
			}
		}, Category.Structure),
		Switch(251, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new ID(src, 1));
			for (int i = 2; i < src.length; i += 2) {
				dst.add(new SignedLiteral(src, i));
				dst.add(new ID(src, i + 1));
			}
		}, Category.Structure),
		Kill(252, false, false, NONE, Category.Structure),
		Return(253, false, false, NONE, Category.Structure),
		ReturnValue(254, false, false, ONE_ID, Category.Structure),
		Unreachable(255, false, false, NONE, Category.Structure),
		LifetimeStart(256, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new UnsignedLiteral(src, 1));
		}, Category.Structure),
		LifetimeStop(257, false, false, (src, dst) -> {
			dst.add(new ID(src, 0));
			dst.add(new UnsignedLiteral(src, 1));
		}, Category.Structure),
		TerminateInvocation(4416, false, false, NONE, Category.Structure),
		// atomic
		AtomicLoad(227, true, true, THREE_IDS, Category.Decleration),
		AtomicStore(228, false, false, FOUR_IDS, Category.SideEffect),
		AtomicExchange(229, true, true, FOUR_IDS, Category.SideEffect),
		AtomicCompareExchange(230, true, true, SIX_IDS, Category.SideEffect),
		AtomicCompareExchangeWeak(231, true, true, SIX_IDS, Category.SideEffect),
		AtomicIIncrement(232, true, true, THREE_IDS, Category.SideEffect),
		AtomicIDecrement(233, true, true, THREE_IDS, Category.SideEffect),
		AtomicIAdd(234, true, true, FOUR_IDS, Category.SideEffect),
		AtomicISub(235, true, true, FOUR_IDS, Category.SideEffect),
		AtomicSMin(236, true, true, FOUR_IDS, Category.SideEffect),
		AtomicUMin(237, true, true, FOUR_IDS, Category.SideEffect),
		AtomicSMax(238, true, true, FOUR_IDS, Category.SideEffect),
		AtomicUMax(239, true, true, FOUR_IDS, Category.SideEffect),
		AtomicAnd(240, true, true, FOUR_IDS, Category.SideEffect),
		AtomicOr(241, true, true, FOUR_IDS, Category.SideEffect),
		AtomicXor(242, true, true, FOUR_IDS, Category.SideEffect),
		AtomicFlagTestAndSet(318, true, true, THREE_IDS, Category.SideEffect),
		AtomicFlagClear(319, false, false, THREE_IDS, Category.SideEffect),
		AtomicFAddEXT(6035, true, true, FOUR_IDS, Category.SideEffect),
		// primitive
		EmitVertex(218, false, false, NONE, Category.SideEffect),
		EndPrimitive(219, false, false, NONE, Category.SideEffect),
		EmitStreamVertex(220, false, false, ONE_ID, Category.SideEffect),
		EndStreamPrimitive(221, false, false, ONE_ID, Category.SideEffect),
		// barrier
		ControlBarrier(224, false, false, THREE_IDS, Category.SideEffect),
		MemoryBarrier(225, false, false, TWO_IDS, Category.SideEffect),
		NamedBarrierInitialize(328, true, true, ONE_ID, Category.Decleration),
		MemoryNamedBarrier(329, false, false, THREE_IDS, Category.SideEffect),
		// group
		// device-side enqueue
		// pipe
		// non uniform
		// reserved
		;

		public final int opCode;
		public final boolean hasId, hasResultId;
		private final OperandProvider provider;
		public final Category category;

		Type(int opCode, boolean hasId, boolean hasResultId, OperandProvider provider, Category category) {
			this.opCode = opCode;
			this.hasId = hasId;
			this.hasResultId = hasResultId;
			this.provider = provider;
			this.category = category;
		}

		public void createOperands(int[] src, Array<Operand<?>> dst) {
			provider.get(src, dst);
		}
	}

	static Map<Integer, Type> typeLookup = new HashMap<>();
	static {
		for (Type type : Type.values()) {
			typeLookup.put(type.opCode, type);
		}
	}

	public final Type type;
	public final int id;
	public final int resultId;
	public final int[] operandWords;
	public final Array<Operand<?>> operands = new Array<>();
	public final Spirv parent;

	public Instruction(Spirv parent, ByteBuffer buffer) {
		this.parent = parent;
		int opCodeWord = buffer.getInt();
		type = typeLookup.get(opCodeWord & 0x0000FFFF);
		int operandCount = ((opCodeWord & 0xFFFF0000) >> 16) - 1;
		if (type.hasId) {
			id = buffer.getInt();
			operandCount--;
		} else {
			id = 0;
		}
		if (type.hasResultId) {
			resultId = buffer.getInt();
			operandCount--;
		} else {
			resultId = 0;
		}
		operandWords = new int[operandCount];
		for (int i = 0; i < operandCount; i++) {
			operandWords[i] = buffer.getInt();
		}

		type.createOperands(operandWords, operands);
	}

	public Instruction(Spirv parent, Type type, int id, int resultId, int... operandWords) {
		this.type = type;
		this.id = id;
		this.resultId = resultId;
		this.operandWords = operandWords;
		this.parent = parent;

		type.createOperands(operandWords, operands);
	}

	public void get(Array<Integer> target) {
		int opCodeWord = type.opCode;
		int instructionSize = 1 + operandWords.length;
		if (type.hasId) {
			instructionSize++;
		}
		if (type.hasResultId) {
			instructionSize++;
		}
		opCodeWord |= instructionSize << 16;
		target.add(opCodeWord);
		if (type.hasId) {
			target.add(id);
		}
		if (type.hasResultId) {
			target.add(resultId);
		}
		for (int i = 0; i < operandWords.length; i++) {
			target.add(operandWords[i]);
		}
	}

	public Instruction resolveId(int id) {
		return parent.instructions.get((op) -> {
			return op.resultId == id;
		});
	}

	public int getInt(int operandIndex) {
		return operandWords[operandIndex];
	}

	public float getFloat(int operandIndex) {
		return Float.intBitsToFloat(operandWords[operandIndex]);
	}

	public Instruction getInstruction(int operandIndex) {
		return resolveId(operandWords[operandIndex]);
	}

	public boolean is(Type type) {
		return this.type == type;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	public String toString(int indentation) {
		StringBuilder sb = new StringBuilder();
		if (type.hasResultId) {
			sb.append("@");
			sb.append(resultId);
		}
		int length = sb.length();
		int paddingCharacters = 6 - length;
		if (!type.hasResultId) {
			paddingCharacters += 3;
		}
		for (int i = 0; i < paddingCharacters; i++) {
			sb.append(" ");
		}
		if (type.hasResultId) {
			sb.append(" = ");
		}
		for (int i = 0; i < indentation; i++) {
			sb.append("    ");
		}
		sb.append(type.name());
		if (type.hasId) {
			sb.append(" ");
			sb.append(resolveId(id).type.name());
		}
		sb.append(" ");
		for (int i = 0; i < operands.length(); i++) {
			sb.append(operands.get(i));
			if (i < operands.length() - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	public boolean isBranch() {
		return is(Type.Branch) || is(Type.BranchConditional);
	}
}
