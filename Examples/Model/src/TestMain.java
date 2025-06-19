
public class TestMain {

	public static void main(String[] args) {
		StateService sc = new StateService();
		
		sc.register("R1A1");
		sc.register("R1A2");
		sc.register("R1A3");
		sc.register("R1A4");
		
		sc.register("R2A1");
		sc.register("R2A2");
		sc.register("R2A3");
		sc.register("R2A4");
		
		sc.select(StateService.SelectDirection.DOWN);
		sc.select(StateService.SelectDirection.DOWN);
		sc.select(StateService.SelectDirection.DOWN);
		
		
		sc.register("R3A1");
		sc.register("R3A2");
		sc.register("R3A3");
		sc.register("R3A4");
		
		sc.select(StateService.SelectDirection.UP);
		sc.select(StateService.SelectDirection.UP);
		sc.select(StateService.SelectDirection.UP);
	}
}
