import java.io.*;
import java.awt.*;
import java.nio.file.Files;
import java.util.*;

public class Main {

	public static void main(String[] args) throws IOException, IllegalStateException {
		System.out.println("Thank you for using this word cloud.");
		try { // try + catch is used to give an error if the file is not found

			//part 0: set up necessary files and introduce the program

			File input = new File("input.txt");
			File filterInput = new File("filter.txt");
			System.out.println("This word cloud ships with example text of Socrates' defense speech.");
			Thread.sleep(100);
			System.out.println("It also ships with a default word filter and a default fonts list, located atfilter.txt and fonts.txt. If you find them unsatisfactory, edit them to your needs. The files contain instructions.");
			Thread.sleep(100);
			System.out.println("If you find any errors in the program, please contact WhitetailAni#1287 on Discord.");

			//part 1: load text into program

			//read text from input.txt
			int lineCount = 0;
			BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
			while (reader.readLine() != null) lineCount++;
			reader.close();
			String strIn;
			String str = new String();
			Scanner sc = new Scanner(input);

			//place this text in a string
			while(sc.hasNextLine()){
				strIn = sc.nextLine();
				str = str + ' ' + strIn; //place a space between lines to prevent merging words later (1)
			}
			sc.close();
			int spaceCount = 0;

			//count number of spaces (not necessary for word cloud, but a useful stat to have)
			for(int i=0; i<str.length(); i++) {
				if(str.charAt(i) == ' ') {
					spaceCount++;
				}
			}

			//corrects for actual number of spaces. the additional number of spaces is equal to the
			//number of lines of text, so this is subtracted from spaceCount. see (1) for why this must be done
			spaceCount = spaceCount - lineCount;

			//converts string into string array
			String[] wordReference = str.split("\\W+"); //this array stores all the words from the provided text. it also contains regex magic hax. DO NOT TOUCH
			int[] wordCountArray = new int[wordReference.length]; //this array stores how many times a word appears
			int wordCount = wordReference.length;

			//changes all words to lowercase
			for(int i=0; i<wordReference.length; i++) {
				wordReference[i] = wordReference[i].toLowerCase(); //converts all words to lowercase
			}

			//debug info
			System.out.println();
			System.out.println("Debug info:");
			System.out.println("The document provided has " + lineCount + " lines of text.");
			System.out.println("The document provided has " + spaceCount + " spaces.");
			System.out.println("The document provided has " + wordCount + " words.");

			//part 2: apply the word filter to the text

			//read the filter into the program
			int filterSkip = 0;
			String filterIn;
			String filter = new String();
			Scanner sc2 = new Scanner(filterInput);
			while(sc2.hasNextLine()){
				if(filterSkip < 2) {
					filterSkip++;
				} else {
					filterIn = sc2.nextLine();
					filter = filter + ' ' + filterIn; //place a space between lines to prevent merging words later (1)
				}
			}
			filterSkip = 0;
			sc2.close();
			String[] filterList = filter.split("\\W+"); //apply the regex magic hax to the filter input

			//filter out all specified words. this technique is used later to check how many times a word is used
			for(int i=0; i<wordReference.length; i++) {
				for(int j=0; j<filterList.length; j++) {
					if(wordReference[i].equals(filterList[j])) {
						wordReference[i] = "";
					}
				}
			}

			//check each word against every other word
			int filteredCount = 0; //lists how many times a word was filtered
			int dupeCount = 0;
			for(int i=0; i<wordReference.length; i++) {
				for(int j=0; j<wordReference.length; j++) {
					if(wordReference[i].equals(wordReference[j])) {
						if(wordReference[i].equals("")) { //checks to see if the word has been filtered and skips counting it
							filteredCount++;
						} else if(wordReference[i].equals(" ")) { //checks if the word number is on the skiplist
							dupeCount++;
						} else {
							wordCountArray[i]++;
							if(i != j) { //erases duplicate word after counting it, to avoid duplicate count
								wordReference[j] = " ";
							}
						}
					}
				}
			}
			System.out.println("The number of times a word was filtered was " + filteredCount + " out of " + wordCount*wordCount + " total checks.");
			System.out.println("The number of times a duplicate word was skipped was " + dupeCount + " out of " + wordCount*wordCount + " total checks.");

			//part 3: determine scoreboard (most appearing words in list)

			int scoreLength = (int)(wordCount*0.01); //determines how many words to report in scoreBoard by taking 1% of word count
			if(scoreLength == 0) {
				scoreLength = 1;
			}
			int[] scoreCountArray = wordCountArray.clone(); //clones wordCountArray to make determining scoreBoard easier
			int[] scoreBoard = new int[scoreLength]; //array that stores index of top <scoreLength> values
			int scoreTest = 0;
			for(int h=0; h<scoreBoard.length; h++) { //finds highest value in array <scoreLength> times
				for (int i=0; i<scoreCountArray.length; i++) {
					if (scoreCountArray[i] > scoreTest) {
						scoreTest = scoreCountArray[i];
						scoreBoard[h] = i;
					}
				}
				scoreCountArray[scoreBoard[h]] = 0; //removes highest value to avoid double counting
				scoreTest = 0; //ensures it can find more than one highest value... spent 3 days figuring this out.
			}

			//debug purposes only, prints out scoreboard
			System.out.println("Scoreboard:");
			for(int i=0; i<scoreLength; i++) {
				System.out.println(wordCountArray[scoreBoard[i]] + " " + wordReference[scoreBoard[i]]);
			}

			//part 4: draw the word cloud with StdDraw

			//prepare stddraw
			StdDraw.setCanvasSize(530,530);
			StdDraw.setScale(0, 100);

			//prepares fonts
			File fontFile = new File("fonts.txt");
			String fontIn;
			String fontList = new String();
			Scanner sc3 = new Scanner(fontFile);
			int filterSkip2 = 0;
			while(sc3.hasNextLine()){
				fontIn = sc3.nextLine();
				fontList = fontList + ' ' + fontIn; //place a space between lines to prevent merging words later (1)
			}
			sc3.close();
			String[] fonts = fontList.split("\\W+");
			//I should probably make this a separate class given that I use it 3 times
			//System.out.println(fontList);
			for(int i=0; i<fonts.length; i++) {
				fonts[i] = fonts[i].replace("0", " ");
			}
			//System.out.println(Arrays.toString(fonts));

			//draw scoreBoard
			int fontPoint = 0;
			for(int i=0; i<scoreBoard.length; i++) {
				if(fontPoint == fonts.length-1) {
					fontPoint = 0;
				} else {
					fontPoint++;
				}
				int xPos = (int)(70*Math.random()+8); //sets the x position equal to Math.random (0.0-1.0) times 70 (0.0-70.0) then adds 8 (8.0-78.0) to prevent clipping.
				int yPos = (int)(5*Math.pow(6,(0.2*i))+(2.4*i)); //sets the ypos equal to 6^(0.2)(i), or the lower a number's frequency, the higher its ypos is. the +2.4i is to ensure no overlap
				int fontSize = (int)(12*Math.log(wordCountArray[scoreBoard[i]])/Math.log(2)); //this sets the font size of each word to a size proportional to its frequency, using logs. I chose log base 2 as it doesn't have insane growth
				StdDraw.setFont(new Font(fonts[fontPoint], Font.BOLD, fontSize)); //sets the font
				StdDraw.text(xPos, yPos, wordReference[scoreBoard[i]]); //draws each word
			}

			//part 5: reset .txt to default
			File reset = new File("gta6.cpp");
			input.delete();
			Files.copy(reset.toPath(), input.toPath());
		}
		catch(IOException e) {
			System.out.println("Please ensure the following:");
			System.out.println("1. That your text to be processed is in a .txt file named input.txt in the /src folder of this program.");
			System.out.println("2. That your word filter list is in a .txt file named filter.txt in the /src folder of this program.");
			System.out.println("3. That your font list is in a .txt file named fonts.txt in the /src folder of this program.");
			System.out.println("4. That you did not touch any other files, such as gta6.cpp.");
		}
		catch (InterruptedException ie) {
			int delay = 0;
			Thread.currentThread().interrupt();
			System.out.println("This message should never show up. If it does, I'm sorry. Something has gone horribly wrong.");
			System.out.println("This may have triggered the Singularity.");
			System.out.println("Also, you should run...");
			for(int i=0; i<100000; i++) {
				for (int j = 0; j < 50000; j++) {
					delay++;
				}
			}
			System.out.println(" right NOW!");
		}
	}
}