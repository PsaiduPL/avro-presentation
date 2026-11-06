import com.example.avro.generated.Employee;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.codec.binary.Hex;


void main() throws IOException {
    System.setProperty("org.apache.avro.SERIALIZABLE_PACKAGES", "com.example.avro.generated");
    SpecificDatumWriter<Employee> writer = new SpecificDatumWriter<>(Employee.getClassSchema());
    SpecificDatumReader<Employee> reader = new SpecificDatumReader<>(Employee.getClassSchema());

    Employee employee = Employee.newBuilder()
                                .setFirstName("Ala")// 06 41 6c 61
                                .setAge(2) // 04
								// email 0 null bytes
                                .build();

    IO.println(new String((Hex.encodeHex(writer.toByteArray(employee)))));

    BinaryDecoder decoder = DecoderFactory.get()
                                          .binaryDecoder(writer.toByteArray(employee), null);
    IO.println(reader.read(null, decoder)
                     .getFirstName());
}