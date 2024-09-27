package io.keepcoding.keeptrivial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MainTrivial {
    private static final int TOPICS_TO_WIN = 5;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Initialize questions
        ArrayList<Question> questions = getQuestions();
        System.out.println("Questions loaded: " + questions.size());

        // Initialize teams
        System.out.print("Enter the number of teams: ");
        int numTeams = scanner.nextInt();
        scanner.nextLine(); 
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < numTeams; i++) {
            System.out.print("Enter the name of team " + (i + 1) + ": ");
            String teamName = scanner.nextLine();
            teams.add(new Team(teamName));
        }


        boolean exit = false;
        int currentTeamIndex = 0;
        while (!exit) {
            Team currentTeam = teams.get(currentTeamIndex);
            System.out.println("It's " + currentTeam.getName() + "'s turn!");


            Question question = selectAvailableQuestion(questions, currentTeam);
            if (question == null) {
                System.out.println("No more available questions for this team.");
                currentTeamIndex = (currentTeamIndex + 1) % teams.size();
                continue;
            }
            question.displayQuestion();


            System.out.print("Enter the answer (1-4) or 'exit' to quit: ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("exit")) {
                exit = true;
            } else if (esTransformableAEntero(answer)) {
                int answerInt = Integer.parseInt(answer);
                if (answerInt == question.getCorrectAnswer()) {
                    System.out.println("Correct! You've won a cheese wedge of " + question.getTopic().getName());
                    currentTeam.addTopic(question.getTopic().getName());
                    if (currentTeam.getWonTopics().size() == TOPICS_TO_WIN) {
                        exit = true;
                        System.out.println(currentTeam.getName() + " has won the game!");
                    }
                } else {
                    System.out.println("Incorrect. The correct answer was " + question.getCorrectAnswer());
                }
            } else {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
            }


            System.out.println("Current Scores:");
            for (Team team : teams) {
                System.out.println(team);
            }


            currentTeamIndex = (currentTeamIndex + 1) % teams.size();
        }


        Team winner = teams.get(0);
        for (Team team : teams) {
            if (team.getWonTopics().size() > winner.getWonTopics().size()) {
                winner = team;
            }
        }
        System.out.println();
        title("Ha ganado: " + winner.getName());
        scanner.close();
    }

    public static void title(String text) {
        int length = text.length();
        printHashtagLine(length + 4);
        System.out.println("# " + text + " #");
        printHashtagLine(length + 4);
    }

    public static void printHashtagLine(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print("#");
        }
        System.out.println();
    }

    public static boolean esTransformableAEntero(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int getRandomInt(int max) {
        return new Random().nextInt(max);
    }

    private static Question selectAvailableQuestion(ArrayList<Question> questions, Team team) {
        List<Question> availableQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (!team.getWonTopics().contains(question.getTopic().getName())) {
                availableQuestions.add(question);
            }
        }
        if (availableQuestions.isEmpty()) {
            return null;
        }
        return availableQuestions.get(getRandomInt(availableQuestions.size()));
    }

    private static ArrayList<Question> getQuestions() {
        ArrayList<Question> list = new ArrayList<>();
        File folder = new File("questions");
        if (!folder.exists()) {
            title("Error al cargar el fichero");
        } else {
            File[] filesList = folder.listFiles();
            for (File file : filesList) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    String topicName = file.getName().substring(0, file.getName().length() - 4);
                    Topic topic = new Topic(topicName);
                    
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        List<String> block = new ArrayList<>();
                        while ((line = br.readLine()) != null) {
                            block.add(line);
                            if (block.size() == 6) { 
                                String questionText = block.get(0);
                                String answer1 = block.get(1);
                                String answer2 = block.get(2);
                                String answer3 = block.get(3);
                                String answer4 = block.get(4);
                                int rightOption = Integer.parseInt(block.get(5));
                                
                                Question question = new Question(questionText, answer1, answer2, answer3, answer4, rightOption, topic);
                                list.add(question);
                                block.clear();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }
}

class Question {
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int correctAnswer;
    private Topic topic;

    public Question(String question, String answer1, String answer2, String answer3, String answer4, int correctAnswer, Topic topic) {
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAnswer = correctAnswer;
        this.topic = topic;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public Topic getTopic() {
        return topic;
    }

    public void displayQuestion() {
        System.out.println("Topic: " + topic.getName());
        System.out.println(question);
        System.out.println("1. " + answer1);
        System.out.println("2. " + answer2);
        System.out.println("3. " + answer3);
        System.out.println("4. " + answer4);
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answer1='" + answer1 + '\'' +
                ", answer2='" + answer2 + '\'' +
                ", answer3='" + answer3 + '\'' +
                ", answer4='" + answer4 + '\'' +
                ", correctAnswer=" + correctAnswer +
                ", topic=" + topic +
                '}';
    }
}

class Topic {
    private String name;

    public Topic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "name='" + name + '\'' +
                '}';
    }
}

class Team {
    private String name;
    private Set<String> wonTopics;

    public Team(String name) {
        this.name = name;
        this.wonTopics = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Set<String> getWonTopics() {
        return wonTopics;
    }

    public void addTopic(String topic) {
        this.wonTopics.add(topic);
    }

    @Override
    public String toString() {
        return name + " (Cheese achieved: " + wonTopics.size() + " - " + wonTopics + ")";
    }
}