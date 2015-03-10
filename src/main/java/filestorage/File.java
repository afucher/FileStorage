package filestorage;

public class File {
	private String filename;

	public File(String filename){
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
