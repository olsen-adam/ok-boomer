public class AltJ {
	
	private String line = 
	"++++++++++++++++ +   +   + S +++            ++    +   +  + ++    +   +   +++    +   +    ++            +++             ++  +       +  ++   ++   ++   ++  G  +++     ++          ++++++ +       +  ++++++++++++++++";
	
	private char[] chars;
	
	int w = 15, h = 14;
	
	static {
        System.loadLibrary("AltTest");
    }
	
	private void start() {
		System.out.println("");
		
		chars = strConvert(line);
		int chosen = 1;
		String sayChoice = "";
			// 1 == DFS
			// 2 == Manhat, no Elim Redund
			// 3 == Manhat, with Elim Redund
			// 4 == A* (unfinished)
			switch(chosen) {
				case 1:
					sayChoice = "DFS"; break;
				case 2:
					sayChoice = "Manhat, no Elim Redund"; break;
				case 3:
					sayChoice = "Manhat, with Elim Redund"; break;
				case 4:
					sayChoice = "A*"; break;
			} System.out.println("User Chose: "+sayChoice);
			
		int[] moves = cFunctionName(chars, w, h, 3);
		System.out.println("Sample moves from C, printed from Java:");
		System.out.println("	(Will be calculated by C)");
		int i=0;
		while (i<moves.length) {
			System.out.println(moves[i]);
			i++;
		}
	}

    public native int[] cFunctionName(char[] mapdata, int w, int h, int searchType);

    public static void main(String[] args) {
        new AltJ().start();
    }
	
	//temp
		private char[] strConvert(String line) {
			char[] chars = new char[line.length()];
			for (int i = 0; i < line.length(); i++) {
				chars[i] = line.charAt(i);
			}
			return chars;
		}
}