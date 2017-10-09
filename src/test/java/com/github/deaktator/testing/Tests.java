package com.github.deaktator.testing;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import com.github.deaktator.testing.p1.ScoreE;
import com.github.deaktator.testing.p2.ScoreN;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(BlockJUnit4ClassRunner.class)
public class Tests {
    private static final int SIZE = 100;

    /**
     * This is the only real differences I can see:
     */
    @Test
    public void testScoreStringFromBuilder() {
        assertEquals("{\"value\": []}",   ScoreE.newBuilder().build().toString());
        assertEquals("{\"value\": null}", ScoreN.newBuilder().build().toString());
    }

    @Test
    public void testToString() {
        assertEquals(new ScoreE().toString(), new ScoreN().toString());
    }

    @Test
    public void testEmpty() throws IOException {
        assertEquals(size(new ScoreE()), size(new ScoreN()));
    }

    @Test
    public void testNonEmpty() throws IOException {
        final ScoreE nonE = new ScoreE();
        nonE.setValue(data());

        final ScoreN nonN = new ScoreN();
        nonN.setValue(data());

        assertEquals(size(nonE), size(nonN));
    }

    @Test
    public void testEmptyToNullEvolution() throws IOException {
        final ScoreN out = serde(ScoreE.class, ScoreN.class, new ScoreE());
        assertNotNull(out);
        assertNull(out.getValue());
    }

    @Test
    public void testNullToEmptyEvolution() throws IOException {
        final ScoreE out = serde(ScoreN.class, ScoreE.class, new ScoreN());
        assertNotNull(out);
        assertNull(out.getValue());
    }

    @Test
    public void testNullToEmptyEvolutionNonEmptyList() throws IOException {
        ScoreN s = new ScoreN();
        s.setValue(data());

        final ScoreE out = serde(ScoreN.class, ScoreE.class, s);
        assertNotNull(out);
        assertNotNull(out.getValue());
        assertEquals(SIZE, out.getValue().size());
        assertEquals(1, out.getValue().get(0).intValue());
    }

    @Test
    public void testEmptyToNullEvolutionNonEmptyList() throws IOException {
        ScoreE s = new ScoreE();
        s.setValue(data());

        final ScoreN out = serde(ScoreE.class, ScoreN.class, s);
        assertNotNull(out);
        assertNotNull(out.getValue());
        assertEquals(SIZE, out.getValue().size());
        assertEquals(1, out.getValue().get(0).intValue());
    }

    private static <B extends SpecificRecord, A extends SpecificRecord>
    A serde(Class<B> bc, Class<A> ac, B b) throws IOException {
        final File data = File.createTempFile("avro", ".dat");
        data.deleteOnExit();

        final SpecificDatumWriter<B> w = new SpecificDatumWriter<B>(bc);
        final DataFileWriter<B> fw = new DataFileWriter<B>(w);
        fw.create(b.getSchema(), data);
        fw.append(b);
        fw.close();

        final DatumReader<A> r = new SpecificDatumReader<A>(ac);
        final DataFileReader<A> fr = new DataFileReader<A>(data, r);
        final A after = fr.next(null);

        fr.close();
        return after;
    }

    private static ArrayList<Integer> data() {
        ArrayList<Integer> is = new ArrayList<Integer>(SIZE);
        for (int i = 1; i <= SIZE; ++i) is.add(i);
        return is;
    }

    private static <A extends SpecificRecord> int size(A a) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryEncoder eE = EncoderFactory.get().binaryEncoder(baos, null);
        GenericDatumWriter wE = new GenericDatumWriter(a.getSchema());
        wE.write(a, eE);
        eE.flush();
        return baos.toByteArray().length;
    }
}
