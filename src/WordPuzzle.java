
import java.util.Scanner;
import java.util.Random;

public class WordPuzzle {
	public static final char HIDDEN_CHAR = '_';
	public static final int MAX_VOCABULARY_SIZE = 3000;

	
	/*
	 * @pre: template is legal for word
	 */
	public static char[] createPuzzleFromTemplate(String word, boolean[] template) { // Q - 1
		char[] res = new char[word.length()];
		for (int i = 0;i < word.length();i++) {
			if(!template[i]) {res[i] = word.charAt(i);}
			else {res[i] = HIDDEN_CHAR;}
		}
		return res;
	}

	public static boolean checkLegalTemplate(String word, boolean[] template) { // Q - 2
		if (word.length() != template.length) {return false;}
		boolean hidden = false;
		boolean shwn = false;
		for (int i = 0; i <template.length; i++) {
			if (template[i] == true) {hidden = true;} 
			if (template[i] == false) {shwn = true;}
			char tmpChar = word.charAt(i);
			boolean tmpBool =  template[i];
			for(int j = i+1; j < template.length; j++) {
				if (word.charAt(j) == tmpChar) {
					if (template[j] != tmpBool) {return false;}
				}

			}
			
		}
		return (hidden && shwn);
	}
	
	/*
	 * @pre: 0 < k < word.lenght(), word.length() <= 10
	 */
	public static boolean[][] getAllLegalTemplates(String word, int k){  // Q - 3
		final int n = word.length();
		final int pow = (int)Math.pow(2,n);
		boolean[][] allTemplates = new boolean[pow][n];
		int legal = 0;
		
		for (int i =0; i < pow; i++) {
			String binary = Integer.toBinaryString(i);
			while (binary.length() < n) {binary = "0"+ binary;}
			boolean [] tmp = new boolean[n];
			char[] ch = binary.toCharArray();
			int count = 0;
			for(int j = 0; j < n; j++) {
				tmp[j] = ch[j] == '0' ? false:true;
				count = ch[j] == '0' ? count :count + 1;
			}
			if (count == k) {
				if(checkLegalTemplate(word,tmp)) {
					allTemplates[legal] = tmp.clone();
					legal++;
					}
				
			}
			
		}
		boolean[][] result = new boolean[legal][n];
		for(int i = 0; i < legal; i++) {
			result[i] = allTemplates[i];
		}
		return result;
	}
	
	
	/*
	 * @pre: puzzle is a legal puzzle constructed from word, guess is in [a...z]
	 */
	public static int applyGuess(char guess, String word, char[] puzzle) { // Q - 4
		if (word.indexOf(guess) == -1) {return 0;}//attention
		int count = 0;
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) == guess) {
				if(puzzle[i] == HIDDEN_CHAR) {
					puzzle[i] = guess;
					count++;
				}
			}
		}
		return count;
}
	

	/*
	 * @pre: puzzle is a legal puzzle constructed from word
	 * @pre: puzzle contains at least one hidden character. 
	 * @pre: there are at least 2 letters that don't appear in word, and the user didn't guess
	 */
	public static char[] getHint(String word, char[] puzzle, boolean[] already_guessed) { // Q - 5
		String alphabet = "abcdefghijklmnopqrstuvwxyz"; 
		char[] res = new char[2];
		char right;
		char wrong;
		Random rnd = new Random();
		int rnd1 = rnd.nextInt(word.length());
		while (puzzle[rnd1] != HIDDEN_CHAR) {
			rnd1 = rnd.nextInt(word.length());
		}
		right = word.charAt(rnd1);
		int rnd2 = rnd.nextInt(26);
		while (word.indexOf(alphabet.charAt(rnd2)) != -1 || already_guessed[rnd2]) {
			rnd2 = rnd.nextInt(26);
		}
		wrong = alphabet.charAt(rnd2);
		if (right < wrong) {
			res[0] = right;
			res[1] = wrong;
		}
		else {
			res[0] = wrong;
			res[1] = right;
		}
		return res;
	}

	

	public static char[] mainTemplateSettings(String word, Scanner inputScanner) { // Q - 6
		printSettingsMessage();
		int n = word.length();
		char[] res = new char[n];
		boolean[] boolTemp = new boolean[n];
		while (true) {
			printSelectTemplate();
			int selection = inputScanner.nextInt();
			if (selection == 1) {
				printSelectNumberOfHiddenChars();
				int hidden = inputScanner.nextInt();
				if (getAllLegalTemplates(word,hidden).length == 0) {
					printWrongTemplateParameters();
					continue;
				}
				boolean[][] templates = getAllLegalTemplates(word,hidden);
				int len = templates.length;
				Random rnd = new Random();
				int rnd1 = rnd.nextInt(len);
				boolTemp = templates[rnd1];
				break;
				
			
			} 
			if (selection == 2) {
				printEnterPuzzleTemplate();
				String puzz = inputScanner.next();
				String[] splitted = puzz.split(",");
				boolean[] tmp = buildBool(new boolean[n], splitted); 
			
				if(!(checkLegalTemplate(word,tmp))) {
					printWrongTemplateParameters();
					continue;
				}
				boolTemp = tmp;
				break;
				
				
			}
			
			
			
		}
		
		res = createPuzzleFromTemplate(word, boolTemp);
		return res;
		
	}
	
	
	
	public static void mainGame(String word, char[] puzzle, Scanner inputScanner){ // Q - 7
		printGameStageMessage();
		int attempts = 3;
		int n = word.length();
		boolean[] already_guessed = new boolean[26];
		for (int i = 0; i < n; i++) {
			if (puzzle[i] == HIDDEN_CHAR) {attempts++;}
		}
		int hidden = attempts - 3;
		
		while (attempts > 0) {
			printPuzzle(puzzle);
			printEnterYourGuessMessage();
			String guess = inputScanner.next();
			
			if(guess.compareTo("H") == 0) {
				printHint(getHint(word,puzzle,already_guessed));
				continue;
			}
			
			else {
				attempts--;
				int changes = applyGuess(guess.charAt(0), word, puzzle);
				already_guessed[(int)guess.charAt(0)-(int)'a'] = true;
				hidden -= changes;
				if (hidden == 0) {
					printWinMessage();
					break;
				}
				
				else if (changes == 0) {//although there is no way that the changes and the hidden are 0 at the same time
					printWrongGuess(attempts);
				}
				
				if (changes != 0) {
					printCorrectGuess(attempts);
				}
			}
			
		}
		
		if (attempts == 0 && hidden != 0) {
			printGameOver();
		}
		
	}
	
	
	
	
	public static boolean[] buildBool(boolean[] tmp,String[] splitted) {
		for (int i = 0; i < splitted.length; i++){
			if(splitted[i].compareTo("X") == 0) {
				tmp[i] = false;
			}
			else {tmp[i] = true;}
		}
		return tmp;
	}
	
				
				


/*************************************************************/
/********************* Don't change this ********************/
/*************************************************************/

	public static void main(String[] args) throws Exception { 
		if (args.length < 1){
			throw new Exception("You must specify one argument to this program");
		}
		String wordForPuzzle = args[0].toLowerCase();
		if (wordForPuzzle.length() > 10){
			throw new Exception("The word should not contain more than 10 characters");
		}
		Scanner inputScanner = new Scanner(System.in);
		char[] puzzle = mainTemplateSettings(wordForPuzzle, inputScanner);
		mainGame(wordForPuzzle, puzzle, inputScanner);
		inputScanner.close();
	}


	public static void printSettingsMessage() {
		System.out.println("--- Settings stage ---");
	}

	public static void printSelectNumberOfHiddenChars(){
		System.out.println("Enter number of hidden characters:");
	}
	public static void printSelectTemplate() {
		System.out.println("Choose a (1) random or (2) manual template:");
	}
	
	public static void printWrongTemplateParameters() {
		System.out.println("Cannot generate puzzle, try again.");
	}
	
	public static void printEnterPuzzleTemplate() {
		System.out.println("Enter your puzzle template:");
	}


	public static void printPuzzle(char[] puzzle) {
		System.out.println(puzzle);
	}


	public static void printGameStageMessage() {
		System.out.println("--- Game stage ---");
	}

	public static void printEnterYourGuessMessage() {
		System.out.println("Enter your guess:");
	}

	public static void printHint(char[] hist){
		System.out.println(String.format("Here's a hint for you: choose either %s or %s.", hist[0] ,hist[1]));

	}
	public static void printCorrectGuess(int attemptsNum) {
		System.out.println("Correct Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWrongGuess(int attemptsNum) {
		System.out.println("Wrong Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWinMessage() {
		System.out.println("Congratulations! You solved the puzzle!");
	}

	public static void printGameOver() {
		System.out.println("Game over!");
	}

}
