package org.racingarena.client.object;

import java.util.Random;

public class QuestionNAnswer {
    private String question;
    private int answer;
    private int duration;
    public QuestionNAnswer() {
        this.answer = 0;
        this.question = "N/A";
        duration = 0;
    }

    public String getQuestion(){
        return question;
    }

    public void setQuestion(String question){
        this.question = question + " =";
    }

    public int getAnswer(){
        return answer;
    }

    public void setAnswer(int answer){
        this.answer = answer;
    }

    public void setDuration(int duration) {
        this.duration = duration - 1;
    }

    public int getDuration() {
        return duration;
    }
}
