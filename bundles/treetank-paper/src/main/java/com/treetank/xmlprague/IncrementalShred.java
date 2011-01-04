package com.treetank.xmlprague;

import java.io.File;
import java.util.Properties;

import com.treetank.access.Database;
import com.treetank.api.IDatabase;
import com.treetank.api.ISession;
import com.treetank.api.IWriteTransaction;
import com.treetank.exception.TTException;
import com.treetank.service.xml.shredder.EShredderInsert;
import com.treetank.service.xml.shredder.XMLShredder;

import org.perfidix.AbstractConfig;
import org.perfidix.Benchmark;
import org.perfidix.annotation.Bench;
import org.perfidix.element.KindOfArrangement;
import org.perfidix.meter.AbstractMeter;
import org.perfidix.meter.Time;
import org.perfidix.meter.TimeMeter;
import org.perfidix.ouput.AbstractOutput;
import org.perfidix.ouput.CSVOutput;
import org.perfidix.ouput.TabularSummaryOutput;
import org.perfidix.result.BenchmarkResult;

public class IncrementalShred {

    private XMLShredder shredderNone;

    private static final int RUNS = 100;

    public static File XMLFile = new File("src" + File.separator + "main" + File.separator + "resources"
        + File.separator + "small.xml");
    public static final File TNKFolder = new File("tnk");

    private int counter = 0;

    public void beforeFirst() {
        final Properties props = new Properties();
        props.put("", "");
    }

//    public void beforeFirstRun
    
    public void beforeShred() {
        try {
            System.out.println("Starting Shredding " + counter);
            final IDatabase database = Database.openDatabase(new File(TNKFolder, XMLFile.getName() + ".tnk"));
            final ISession session = database.getSession();
            final IWriteTransaction wtx = session.beginWriteTransaction();
//            if (wtx.moveToFirstChild()) {
//                wtx.remove();
//            }
            shredderNone =
                new XMLShredder(wtx, XMLShredder.createReader(XMLFile), EShredderInsert.ADDASFIRSTCHILD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bench(beforeEachRun = "beforeShred", afterEachRun = "tearDown", runs = RUNS)
    public void benchInsert() {
        try {
            shredderNone.call();
        } catch (TTException e) {
            e.printStackTrace();
        }
    }

    public void tearDown() {
        try {
            System.out.println("Finished Shredding Version " + counter);
            counter++;
            Database.forceCloseDatabase(new File(TNKFolder, XMLFile.getName() + ".tnk"));
        } catch (TTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {

        if (args.length != 2) {
            System.out
                .println("Please use java -jar JAR \"folder with xmls to parse\" \"folder to write csv\"");
            System.exit(-1);
        }

        // Argument is a folder with only XML in there. For each XML one benchmark should be executed.
        final File filetoshred = new File(args[0]);
        final File[] files = filetoshred.listFiles();
        final File filetoexport = new File(args[1]);
        for (final File currentFile : files) {
            XMLFile = currentFile;
            System.out.println("Starting benchmark for " + XMLFile.getName());
            final int index = currentFile.getName().lastIndexOf(".");
            final File folder = new File(filetoexport, currentFile.getName().substring(0, index));
            folder.mkdirs();
            final FilesizeMeter meter =
                new FilesizeMeter(new File(new File(new File(TNKFolder, XMLFile.getName() + ".tnk"), "tt"),
                    "tt.tnk"));

            final Benchmark bench = new Benchmark(new AbstractConfig(RUNS, new AbstractMeter[] {
                meter, new TimeMeter(Time.MilliSeconds)
            }, new AbstractOutput[0], KindOfArrangement.SequentialMethodArrangement, 1.0d) {
            });

            bench.add(IncrementalShred.class);
            final BenchmarkResult res = bench.run();
            new TabularSummaryOutput(System.out).visitBenchmark(res);
            new CSVOutput(folder).visitBenchmark(res);
            System.out.println("Finished benchmark for " + XMLFile.getName());
        }

    }

}