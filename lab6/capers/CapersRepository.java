package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author DONE
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * DONE: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(CWD,".capers"); // Done: Hint: look at the `join` function in Utils

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        // Done by wzx

        // make a dir ./capers
        CAPERS_FOLDER.mkdir();
        // make a dir ./capers/dogs
        Dog.DOG_FOLDER.mkdir();

        // make a file ./capers/story.txt
        File storytxt = new File("./.capers/story.txt");
        try {
            storytxt.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // Done by wzx
        File story_txt = new File("./.capers/story.txt");
        String old_text = readContentsAsString(story_txt);
        if(old_text.isEmpty()) {
            old_text = text;
        }
        else {
            old_text = old_text + "\n" + text;
        }
        Utils.writeContents(story_txt,old_text);
        System.out.println(old_text);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        // DONE
        Dog newdog = new Dog(name, breed, age);
        newdog.saveDog();
        System.out.println(newdog.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        // DONE
        Dog olddog = Dog.fromFile(name);
        olddog.haveBirthday();
        olddog.saveDog();
    }
}
