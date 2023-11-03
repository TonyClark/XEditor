package find;

public class Result {

	private TreeItem item;

	public Result(TreeItem item) {
		this.item = item;
	}

	public String toString() {
		return item.fullLabel();
	}

	public TreeItem getItem() {
		return item;
	}

	public int compare(Result r) {
		return toString().compareTo(r.toString());
	}

}
