public class Robot {


	private String name;
	
	private boolean A1;
	private boolean A2;
	private boolean A3;
	private boolean A4;
	
	public Robot(String name){
		this.name = name;
		A1 = A2 = A3 = A4 = false;
	}
	
	public boolean isAvailable() {
		return A1 && A2 && A3 && A4;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isA1() {
		return A1;
	}

	public void setA1(boolean a1) {
		A1 = a1;
	}

	public boolean isA2() {
		return A2;
	}

	public void setA2(boolean a2) {
		A2 = a2;
	}

	public boolean isA3() {
		return A3;
	}

	public void setA3(boolean a3) {
		A3 = a3;
	}

	public boolean isA4() {
		return A4;
	}

	public void setA4(boolean a4) {
		A4 = a4;
	}
	
	
}
