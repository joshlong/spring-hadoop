package org.springframework.hadoop.test;

import static org.junit.Assert.assertTrue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.hadoop.JobTemplate;
import org.springframework.hadoop.util.PropertiesUtils;
import org.springframework.util.ClassUtils;

public class WordCountIntegrationTests {

	@Rule
	public HadoopSetUp setUp = HadoopSetUp.preferClusterRunning("localhost", 9001);

	private ConfigurableApplicationContext context;

	private JobTemplate jobTemplate;

	@Before
	public void init() throws Exception {
		jobTemplate = new JobTemplate();
		if (setUp.isClusterOnline()) {
			setUp.setJarFile("target/spring-hadoop-core-1.0.0.BUILD-SNAPSHOT-test.jar");
			jobTemplate.setExtraConfiguration(setUp.getExtraConfiguration());
		}
		jobTemplate.setVerbose(true);
		setUp.copy("src/test/resources/input", "target/input");
		setUp.delete("target/output");
	}

	@After
	public void close() throws Exception {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public void testNativeJob() throws Exception {
		Configuration conf = setUp.getConfiguration();
		Job job = new Job(conf, "nativeWordCount");
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path("target/input"));
		FileOutputFormat.setOutputPath(job, new Path("target/output"));
		assertTrue(job.waitForCompletion(true));
	}

	@Test
	public void testXmlConfiguredJob() throws Exception {
		assertTrue(jobTemplate.run("classpath:/jobs/autowired-job-context.xml"));
	}
	
	@Test
	public void testClassConfiguredJob() throws Exception {
		assertTrue(jobTemplate.run(JobConfiguration.class));
	}
	
	@Test
	public void testLiteConfiguredJob() throws Exception {
		assertTrue(jobTemplate.run(LiteConfiguration.class));
	}
	
	@Test
	public void testPojoConfiguredJob() throws Exception {
		assertTrue(jobTemplate.run(PojoConfiguration.class));
	}
	
	@Test
	public void tesKitchenSinkJob() throws Exception {
		assertTrue(jobTemplate.run(KitchenSinkConfiguration.class));
	}
	
	@Test
	public void testPackageConfiguredJob() throws Exception {
		assertTrue(jobTemplate.run(ClassUtils.getPackageName(getClass())+".config"));
	}
	
	@Test
	public void testBootstrapJob() throws Exception {
		jobTemplate.setBootstrapProperties(PropertiesUtils.stringToProperties("tokenizer.ref=wordTokenizer"));
		assertTrue(jobTemplate.run("classpath:/jobs/bootstrap-job-context.xml"));
	}
	
	public static void main(String[] args) throws Exception {
		new WordCountIntegrationTests().testXmlConfiguredJob();
	}

}
