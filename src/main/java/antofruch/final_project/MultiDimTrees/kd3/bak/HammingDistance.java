// Hamming distance metric class

package antofruch.final_project.MultiDimTrees.kd3.bak;

class HammingDistance extends DistanceMetric {
    
    protected double distance(double [] a, double [] b)  {

	double dist = 0;

	for (int i=0; i<a.length; ++i) {
	    double diff = (a[i] - b[i]);
	    dist += Math.abs(diff);
	}

	return dist;
    }     
}
