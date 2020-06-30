package utils;

public class Pair<L,R> {
	private L left;
	private R right;
	
	public Pair(L left, R right) {
		assert left != null;
		assert right != null;
		
		this.left = left;
		this.right = right;
	}
	
	public L getLeft() { return this.left; }
	public R getRight() { return this.right; }
	public void setLeft(L left) { this.left = left; }
	public void setRight(R right) {this.right = right; }
	
	@Override
	public int hashCode() { return left.hashCode() ^ right.hashCode(); }
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) return false;
		Pair pairo = (Pair) o;
		return (this.left.equals(pairo.left) && this.right.equals(pairo.right));
	}
	
	@Override
	public String toString() {
		return "["+this.left +", " + this.right+"]";
	}
}
