import weka.core.*;
import weka.clusterers.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.AddCluster;

public class Clusterer {

	public static void main(String[] args) {
	
		//LET THE FIRST STRING BE A LIST OF ALL THE FEATURES
		
		String featString = args[0];
		featString = featString.substring(1, featString.length()-1); //get rid of []
		String[] features = featString.split(", ");
		
		FastVector attributes = new FastVector(features.length);
		
		//name all of the attributes
		for(int i=0; i<features.length; i++) {
			attributes.addElement(new Attribute(features[i]));
		}
		
		Instances list = new Instances("Instance List", attributes, 100);
		
		//loop through args[1] to end, making instances
		for(int i=1; i<args.length; i++) {
			//at some point I will need to cast as a numeric type...
			String vecString = args[i].substring(1, args[i].length()-1); //get rid of []
			String[] vec = vecString.split(", ");
			Instance temp = new Instance(features.length);
			for(int j=0; j<vec.length; j++) {
				temp.setValue(j, Double.parseDouble(vec[j]));
			}
			list.add(temp);
		}
		
		//cluster the instances using DBScan
		try {
			DBScan c = new DBScan();
			c.buildClusterer(list);
			
			//append cluster name to the end of each feature vector
			AddCluster filter = new AddCluster();
			filter.setClusterer(c);
			filter.setInputFormat(list);
			Instances clusteredList = Filter.useFilter(list, filter);

			//print out just the cluster information
			for(int i=0; i<clusteredList.numInstances(); i++) {
				System.out.println(clusteredList.instance(i).toString(clusteredList.numAttributes()-1));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
