/**
 * 
 */
package de.kjEngine.graphics.shader.parser.spirv;

import java.util.HashMap;
import java.util.Map;

import de.kjEngine.graphics.shader.parser.spirv.Instruction.Type;
import de.kjEngine.util.container.Array;

/**
 * @author konst
 *
 */
public class Operand<T> {

	public static class SignedLiteral extends Operand<Integer> {

		public SignedLiteral(int[] data, int offset) {
			super(data[offset], 1);
		}
	}

	public static class UnsignedLiteral extends Operand<Long> {

		public UnsignedLiteral(int[] data, int offset) {
			super(Integer.toUnsignedLong(data[offset]), 1);
		}
	}

	public static class FloatLiteral extends Operand<Float> {

		public FloatLiteral(int[] data, int offset) {
			super(Float.intBitsToFloat(data[offset]), 1);
		}
	}

	public static class ID extends Operand<Integer> {

		public ID(int[] data, int offset) {
			super(data[offset], 1);
		}

		@Override
		public String toString() {
			return "@" + super.toString();
		}
	}

	public static class StringLiteral extends Operand<String> {

		public StringLiteral(int[] data, int offset) {
			Array<Integer> bytes = new Array<>();
			int byteIndex = 0;
			for (;; byteIndex++) {
				int currentByte = (data[byteIndex / 4 + offset] >> (byteIndex & 0b11) * 8) & 0xFF;
				if (currentByte == 0) {
					break;
				}
				bytes.add(currentByte);
			}
			length = byteIndex / 4;
			if (byteIndex % 4 != 0) {
				length++;
			}
			byte[] string = new byte[bytes.length()];
			for (int i = 0; i < string.length; i++) {
				string[i] = (byte) bytes.get(i).intValue();
			}
			value = new String(string);
		}
	}

	public static enum ExtendedType {
		Round(1),
		RoundEven(2),
		Trunc(3),
		FAbs(4),
		SAbs(5),
		FSign(6),
		SSign(7),
		Floor(8),
		Ceil(9),
		Fract(10),
		Radians(11),
		Degrees(12),
		Sin(13),
		Cos(14),
		Tan(15),
		Asin(16),
		Acos(17),
		Atan(18),
		Sinh(19),
		Cosh(20),
		Tanh(21),
		Asinh(22),
		Acosh(23),
		Atanh(24),
		Atan2(25),
		Pow(26),
		Exp(27),
		Log(28),
		Exp2(29),
		Log2(30),
		Sqrt(31),
		InverseSqrt(32),
		Determinant(33),
		MatrixInverse(34),
		Modf(35),
		ModfStruct(36),
		FMin(37),
		UMin(38),
		SMin(39),
		FMax(40),
		UMax(41),
		SMax(42),
		FClamp(43),
		UClamp(44),
		SClamp(45),
		FMix(46),
		Step(48),
		SmoothStep(49),
		Fma(50),
		Frexp(51),
		FrexpStruct(52),
		Ldexp(53),
		PackSnorm4x8(54),
		PackUnorm4x8(55),
		PackSnorm2x16(56),
		PackUnorm2x16(57),
		PackHalf2x16(58),
		PackDouble2x32(59),
		UnpackSnorm2x16(60),
		UnpackUnorm2x16(61),
		UnpackHalf2x16(62),
		UnpackSnorm4x8(63),
		UnpackUnorm4x8(64),
		UnpackDouble2x32(65),
		Length(66),
		Distance(67),
		Cross(68),
		Normalize(69),
		FaceForward(70),
		Reflect(71),
		Refract(72),
		FindILsb(73),
		FindSMsb(74),
		FindUMsb(75),
		InterpolateAtCentroid(76),
		InterpolateAtSample(77),
		InterpolateAtOffset(78),
		NMin(79),
		NMax(80),
		NClamp(81);

		public final int opCode;

		private ExtendedType(int opCode) {
			this.opCode = opCode;
		}
	}

	private static Map<Integer, ExtendedType> extendedTypeLookup = new HashMap<>();
	static {
		for (ExtendedType type : ExtendedType.values()) {
			extendedTypeLookup.put(type.opCode, type);
		}
	}

	public static class ExtendedTypeLiteral extends Operand<ExtendedType> {

		public ExtendedTypeLiteral(int[] data, int offset) {
			super(extendedTypeLookup.get(data[offset]), 1);
		}
	}

	public static enum LoopControl {
		None(0x0),
		Unroll(0x1),
		DontUnroll(0x2),
		DependencyInfinite(0x4),
		DependencyLength(0x8),
		MinIterations(0x10),
		MaxIterations(0x20),
		IterationMultiple(0x40),
		PeelCount(0x80),
		PartialCount(0x100);

		public final int mask;

		private LoopControl(int mask) {
			this.mask = mask;
		}
	}

	public static class LoopControlLiteral extends Operand<Integer> {

		public LoopControlLiteral(int[] data, int offset) {
			super(data[offset], 1);
		}

		public boolean has(LoopControl control) {
			return (value & control.mask) != 0;
		}

		@Override
		public String toString() {
			Array<LoopControl> bits = new Array<>();
			for (LoopControl bit : LoopControl.values()) {
				if (has(bit)) {
					bits.add(bit);
				}
			}
			return bits.toString();
		}
	}

	public static enum SelectionControl {
		None(0x0),
		Flatten(0x1),
		DontFlatten(0x2);

		public final int mask;

		private SelectionControl(int mask) {
			this.mask = mask;
		}
	}

	public static class SelectionControlLiteral extends Operand<Integer> {

		public SelectionControlLiteral(int[] data, int offset) {
			super(data[offset], 1);
		}

		public boolean has(SelectionControl control) {
			return (value & control.mask) != 0;
		}

		@Override
		public String toString() {
			Array<SelectionControl> bits = new Array<>();
			for (SelectionControl bit : SelectionControl.values()) {
				if (has(bit)) {
					bits.add(bit);
				}
			}
			return bits.toString();
		}
	}

	public static enum Decoration {
		RelaxedPrecision(0),
		SpecId(1),
		Block(2),
		BufferBlock(3),
		RowMajor(4),
		ColMajor(5),
		ArrayStride(6),
		MatrixStride(7),
		GLSLShared(8),
		GLSLPacked(9),
		CPacked(10),
		BuiltIn(11),
		NoPerspective(13),
		Flat(14),
		Patch(15),
		Centroid(16),
		Sample(17),
		Invariant(18),
		Restrict(19),
		Aliased(20),
		Volatile(21),
		Constant(22),
		Coherent(23),
		NonWritable(24),
		NonReadable(25),
		Uniform(26),
		UniformId(27),
		SaturatedConversion(28),
		Stream(29),
		Location(30),
		Component(31),
		Index(32),
		Binding(33),
		DescriptorSet(34),
		Offset(35),
		XfbBuffer(36),
		XfbStride(37),
		FuncParamAttr(38),
		FPRoundingMode(39),
		FPFastMathMode(40),
		LinkageAttributes(41),
		NoContraction(42),
		InputAttachmentIndex(43),
		Alignment(44),
		MaxByteOffset(45),
		AlignmentId(46),
		MaxByteOffsetId(47),
		NoSignedWrap(4469),
		NoUnsignedWrap(4470);

		public final int id;

		private Decoration(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, Decoration> decorationLookup = new HashMap<>();
	static {
		for (Decoration dec : Decoration.values()) {
			decorationLookup.put(dec.id, dec);
		}
	}

	public static class DecorationLiteral extends Operand<Decoration> {

		public DecorationLiteral(int[] data, int offset) {
			super(decorationLookup.get(data[offset]), 1);
		}
	}

	public static enum AddressingModel {
		Logical(0),
		Physical32(1),
		Physical64(2),
		PhysicalStorageBuffer64(5348);

		public final int id;

		private AddressingModel(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, AddressingModel> addressingModelLookup = new HashMap<>();
	static {
		for (AddressingModel m : AddressingModel.values()) {
			addressingModelLookup.put(m.id, m);
		}
	}

	public static class AddressingModelLiteral extends Operand<AddressingModel> {

		public AddressingModelLiteral(int[] data, int offset) {
			super(addressingModelLookup.get(data[offset]), 1);
		}
	}

	public static enum MemoryModel {
		Simple(0),
		GLSL450(1),
		OpenCL(2),
		Vulkan(3);

		public final int id;

		private MemoryModel(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, MemoryModel> memoryModelLookup = new HashMap<>();
	static {
		for (MemoryModel m : MemoryModel.values()) {
			memoryModelLookup.put(m.id, m);
		}
	}

	public static class MemoryModelLiteral extends Operand<MemoryModel> {

		public MemoryModelLiteral(int[] data, int offset) {
			super(memoryModelLookup.get(data[offset]), 1);
		}
	}

	public static enum ExecutionModel {
		Vertex(0),
		TessellationControl(1),
		TessellationEvaluation(2),
		Geometry(3),
		Fragment(4),
		GLCompute(5),
		Kernel(6),
		TaskNV(5267),
		MeshNV(5268),
		RayGeneration(5313),
		Intersection(5314),
		AnyHit(5315),
		ClosestHit(5316),
		Miss(5317),
		Callable(5318);

		public final int id;

		private ExecutionModel(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, ExecutionModel> executionModelLookup = new HashMap<>();
	static {
		for (ExecutionModel m : ExecutionModel.values()) {
			executionModelLookup.put(m.id, m);
		}
	}

	public static class ExecutionModelLiteral extends Operand<ExecutionModel> {

		public ExecutionModelLiteral(int[] data, int offset) {
			super(executionModelLookup.get(data[offset]), 1);
		}
	}

	public static enum Capability {
		Matrix(0),
		Shader(1),
		Geometry(2),
		Tessellation(3),
		Addresses(4),
		Linkage(5),
		Kernel(6),
		Vector16(7),
		Float16Buffer(8),
		Float16(9),
		Float64(10),
		Int64(11),
		Int64Atomics(12),
		ImageBasic(13),
		ImageReadWrite(14),
		ImageMipmap(15),
		Pipes(17),
		Groups(18),
		DeviceEnqueue(19),
		LiteralSampler(20),
		AtomicStorage(21),
		Int16(22),
		TessellationPointSize(23),
		GeometryPointSize(24),
		ImageGatherExtended(25),
		StorageImageMultisample(27),
		UniformBufferArrayDynamicIndexing(28),
		SampledImageArrayDynamicIndexing(29),
		StorageBufferArrayDynamicIndexing(30),
		StorageImageArrayDynamicIndexing(31),
		ClipDistance(32),
		CullDistance(33),
		ImageCubeArray(34),
		SampleRateShading(35),
		ImageRect(36),
		SampledRect(37),
		GenericPointer(38),
		Int8(39),
		InputAttachment(40),
		SparseResidency(41),
		MinLod(42),
		Sampled1D(43),
		Image1D(44),
		SampledCubeArray(45),
		SampledBuffer(46),
		ImageBuffer(47),
		ImageMSArray(48),
		StorageImageExtendedFormats(49),
		ImageQuery(50),
		DerivativeControl(51),
		InterpolationFunction(52),
		TransformFeedback(53),
		GeometryStreams(54),
		StorageImageReadWithoutFormat(55),
		StorageImageWriteWithoutFormat(56),
		MultiViewport(57),
		SubgroupDispatch(58),
		NamedBarrier(59),
		PipeStorage(60),
		GroupNonUniform(61),
		// TODO: finish
		;

		public final int id;

		private Capability(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, Capability> capabilityLookup = new HashMap<>();
	static {
		for (Capability m : Capability.values()) {
			capabilityLookup.put(m.id, m);
		}
	}

	public static class CapabilityLiteral extends Operand<Capability> {

		public CapabilityLiteral(int[] data, int offset) {
			super(capabilityLookup.get(data[offset]), 1);
		}
	}

	public static enum Dim {
		_1D(0),
		_2D(1),
		_3D(2),
		Cube(3),
		Rect(4),
		Buffer(5),
		SubpassData(6);

		public final int id;

		private Dim(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, Dim> dimLookup = new HashMap<>();
	static {
		for (Dim m : Dim.values()) {
			dimLookup.put(m.id, m);
		}
	}

	public static class DimLiteral extends Operand<Dim> {

		public DimLiteral(int[] data, int offset) {
			super(dimLookup.get(data[offset]), 1);
		}
	}

	public static enum ImageFormat {
		Unknown(0),
		Rgba32f(1),
		Rgba16f(2),
		R32f(3),
		Rgba8(4),
		Rgba8Snorm(5),
		Rg32f(6),
		Rg16f(7),
		R11fG11fB10f(8),
		R16f(9),
		Rgba16(10),
		Rgb10A2(11),
		Rg16(12),
		Rg8(13),
		R16(14),
		R8(15),
		Rgba16Snorm(16),
		Rg16Snorm(17),
		Rg8Snorm(18),
		R16Snorm(19),
		R8Snorm(20),
		Rgba32i(21),
		Rgba16i(22),
		Rgba8i(23),
		R32i(24),
		Rg32i(25),
		Rg16i(26),
		Rg8i(27),
		R16i(28),
		R8i(29),
		Rgba32ui(30),
		Rgba16ui(31),
		Rgba8ui(32),
		R32ui(33),
		Rgb10a2ui(34),
		Rg32ui(35),
		Rg16ui(36),
		Rg8ui(37),
		R16ui(38),
		R8ui(39),
		R64ui(40),
		R64i(41);

		public final int id;

		private ImageFormat(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, ImageFormat> imageFormatLookup = new HashMap<>();
	static {
		for (ImageFormat m : ImageFormat.values()) {
			imageFormatLookup.put(m.id, m);
		}
	}

	public static class ImageFormatLiteral extends Operand<ImageFormat> {

		public ImageFormatLiteral(int[] data, int offset) {
			super(imageFormatLookup.get(data[offset]), 1);
		}
	}

	public static enum AccessQualifier {
		ReadOnly(0),
		WriteOnly(1),
		ReadWrite(2);

		public final int id;

		private AccessQualifier(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, AccessQualifier> accessQualifierLookup = new HashMap<>();
	static {
		for (AccessQualifier m : AccessQualifier.values()) {
			accessQualifierLookup.put(m.id, m);
		}
	}

	public static class AccessQualifierLiteral extends Operand<AccessQualifier> {

		public AccessQualifierLiteral(int[] data, int offset) {
			super(accessQualifierLookup.get(data[offset]), 1);
		}
	}

	public static enum StorageClass {
		UniformConstant(0),
		Input(1),
		Uniform(2),
		Output(3),
		Workgroup(4),
		CrossWorkgroup(5),
		Private(6),
		Function(7),
		Generic(8),
		PushConstant(9),
		AtomicCounter(10),
		Image(11),
		StorageBuffer(12);

		public final int id;

		private StorageClass(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, StorageClass> srorageClassLookup = new HashMap<>();
	static {
		for (StorageClass m : StorageClass.values()) {
			srorageClassLookup.put(m.id, m);
		}
	}

	public static class StorageClassLiteral extends Operand<StorageClass> {

		public StorageClassLiteral(int[] data, int offset) {
			super(srorageClassLookup.get(data[offset]), 1);
		}
	}

	public static enum SamplerAddressingMode {
		None(0),
		ClampToEdge(1),
		Clamp(2),
		Repeat(3),
		RepeatMirrored(4);

		public final int id;

		private SamplerAddressingMode(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, SamplerAddressingMode> samplerAddressingModeLookup = new HashMap<>();
	static {
		for (SamplerAddressingMode m : SamplerAddressingMode.values()) {
			samplerAddressingModeLookup.put(m.id, m);
		}
	}

	public static class SamplerAddressingModeLiteral extends Operand<SamplerAddressingMode> {

		public SamplerAddressingModeLiteral(int[] data, int offset) {
			super(samplerAddressingModeLookup.get(data[offset]), 1);
		}
	}
	
	public static enum SamplerFilterMode {
		Nearest(0),
		Linear(1);
		
		public final int id;

		private SamplerFilterMode(int id) {
			this.id = id;
		}
	}
	
	private static Map<Integer, SamplerFilterMode> samplerFilterModeLookup = new HashMap<>();
	static {
		for (SamplerFilterMode m : SamplerFilterMode.values()) {
			samplerFilterModeLookup.put(m.id, m);
		}
	}

	public static class SamplerFilterModeLiteral extends Operand<SamplerFilterMode> {

		public SamplerFilterModeLiteral(int[] data, int offset) {
			super(samplerFilterModeLookup.get(data[offset]), 1);
		}
	}
	
	public static class InstructionTypeLiteral extends Operand<Type> {

		public InstructionTypeLiteral(int[] data, int offset) {
			super(Instruction.typeLookup.get(data[offset]), 1);
		}
	}
	
	public static enum Memory {
		None(0x0),
		Volatile(0x1),
		Aligned(0x2),
		Nontemporal(0x4),
		MakePointerAvailable(0x8),
		MakePointerVisible(0x10),
		NonPrivatePointer(0x20);
		
		public final int mask;

		private Memory(int mask) {
			this.mask = mask;
		}
	}
	
	public static class MemoryLiteral extends Operand<Integer> {

		public MemoryLiteral(int[] data, int offset) {
			super(data[offset], 1);
		}

		public boolean has(Memory mem) {
			return (value & mem.mask) != 0;
		}

		@Override
		public String toString() {
			Array<Memory> bits = new Array<>();
			for (Memory bit : Memory.values()) {
				if (has(bit)) {
					bits.add(bit);
				}
			}
			return bits.toString();
		}
	}
	
	public static enum FunctionControl {
		None(0x0),
		Inline(0x1),
		DontInline(0x2),
		Pure(0x4),
		Const(0x8);
		
		public final int mask;

		private FunctionControl(int mask) {
			this.mask = mask;
		}
	}
	
	public static class FunctionControlLiteral extends Operand<Integer> {

		public FunctionControlLiteral(int[] data, int offset) {
			super(data[offset], 1);
		}

		public boolean has(FunctionControl control) {
			return (value & control.mask) != 0;
		}

		@Override
		public String toString() {
			Array<FunctionControl> bits = new Array<>();
			for (FunctionControl bit : FunctionControl.values()) {
				if (has(bit)) {
					bits.add(bit);
				}
			}
			return bits.toString();
		}
	}
	
	public static enum Image {
		None(0x0),
		Bias(0x1),
		Lod(0x2),
		Grad(0x4),
		ConstOffset(0x8),
		Offset(0x10),
		ConstOffsets(0x20),
		Sample(0x40),
		MinLod(0x80),
		MakeTexelAvailable(0x100),
		MakeTexelVisible(0x200),
		NonPrivateTexel(0x400),
		VolatileTexel(0x800),
		SignExtend(0x1000),
		ZeroExtend(0x2000);
		
		public final int mask;

		private Image(int mask) {
			this.mask = mask;
		}
	}
	
	public static class ImageLiteral extends Operand<Integer> {

		public ImageLiteral(int[] data, int offset) {
			super(data[offset], 1);
		}

		public boolean has(Image bit) {
			return (value & bit.mask) != 0;
		}

		@Override
		public String toString() {
			Array<Image> bits = new Array<>();
			for (Image bit : Image.values()) {
				if (has(bit)) {
					bits.add(bit);
				}
			}
			return bits.toString();
		}
	}
	
	public static enum ExecutionMode {
		Invocations(0),
		SpacingEqual(1),
		SpacingFractionalEven(2),
		SpacingFractionalOdd(3),
		VertexOrderCw(4),
		VertexOrderCcw(5),
		PixelCenterInteger(6),
		OriginUpperLeft(7),
		OriginLowerLeft(8),
		EarlyFragmentTests(9),
		PointMode(10),
		Xfb(11),
		DepthReplacing(12),
		DepthGreater(14),
		DepthLess(15),
		DepthUnchanged(16),
		LocalSize(17),
		LocalSizeHint(18),
		InputPoints(19),
		InputLines(20),
		InputLinesAdjacency(21),
		Triangles(22),
		InputTrianglesAdjacency(23),
		Quads(24),
		Isolines(25),
		OutputVertices(26),
		OutputPoints(27),
		OutputLineStrip(28),
		OutputTriangleStrip(29),
		VecTypeHint(30),
		ContractionOff(31),
		Initializer(33),
		Finalizer(34),
		SubgroupSize(35),
		SubgroupsPerWorkgroup(36),
		SubgroupsPerWorkgroupId(37),
		LocalSizeId(38),
		LocalSizeHintId(39),
		PostDepthCoverage(4446),
		DenormPreserve(4459),
		DenormFlushToZero(4460),
		SignedZeroInfNanPreserve(4461),
		RoundingModeRTE(4462),
		RoundingModeRTZ(4463);

		public final int id;

		private ExecutionMode(int id) {
			this.id = id;
		}
	}

	private static Map<Integer, ExecutionMode> executionModeLookup = new HashMap<>();
	static {
		for (ExecutionMode m : ExecutionMode.values()) {
			executionModeLookup.put(m.id, m);
		}
	}

	public static class ExecutionModeLiteral extends Operand<ExecutionMode> {

		public ExecutionModeLiteral(int[] data, int offset) {
			super(executionModeLookup.get(data[offset]), 1);
		}
	}

	protected T value;
	protected int length;

	public Operand() {
	}

	public Operand(T value, int length) {
		this.value = value;
		this.length = length;
	}

	public T getValue() {
		return value;
	}

	public int getLength() {
		return length;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
