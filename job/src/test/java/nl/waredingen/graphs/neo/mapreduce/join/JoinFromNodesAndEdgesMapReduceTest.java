package nl.waredingen.graphs.neo.mapreduce.join;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import nl.waredingen.graphs.neo.mapreduce.DualInputMapReduceDriver;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Before;
import org.junit.Test;

public class JoinFromNodesAndEdgesMapReduceTest {

	private DualInputMapReduceDriver<NullWritable,BytesWritable,NullWritable,BytesWritable,Text,Text,NullWritable,Text> driver;
	private List<Pair<NullWritable, Text>> output;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		driver = new DualInputMapReduceDriver<NullWritable,BytesWritable,NullWritable,BytesWritable, Text, Text, NullWritable, Text>();
		driver.setFirstMapper(new JoinNodesMapper());
		driver.setSecondMapper(new JoinFromEdgesMapper());
		driver.setReducer(new JoinNodesAndEdgesReducer());
		driver.setKeyGroupingComparator(new NodeKeyGroupingComparator());
		driver.setKeyOrderComparator(new NodeAndEdgeKeyComparator());
	}

	@Test
	public void shouldjoinFromNodeAndEdge() throws Exception {
		Text nodeA = new Text("0	A	1");
		BytesWritable nodeAInput = new BytesWritable(nodeA.getBytes());
		Text nodeB = new Text("1	B	3");
		BytesWritable nodeBInput = new BytesWritable(nodeB.getBytes());
		Text edge = new Text("0	A	B	5");
		BytesWritable edgeInput = new BytesWritable(edge.getBytes());
		driver.withFirstInput(NullWritable.get(), nodeAInput).addInput(NullWritable.get(), nodeBInput);
		output = driver.withSecondInput(NullWritable.get(), edgeInput).run();

		assertThat(output.size(), is(1));
		assertThat(output.get(0).getFirst(), equalTo(NullWritable.get()));
		assertThat(output.get(0).getSecond(), equalTo(new Text("0	A	B	5	0	1")));
	}
}
