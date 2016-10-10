package com.anchronize.sudomap.objects;

/**
 * Created by tianlinz on 4/2/16.
 */
public class Post {

    public Post(String ID, String message, User author){
        postID = ID;
        this.message = message;
        this.author = author;
    }

    public String getPostID() {
        return postID;
    }

    public String getMessage() {
        return message;
    }

    public User getAuthor() {
        return author;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void incrementVoteCount() {
        this.voteCount++;
    }

    public void decrementVoteCount(){
        this.voteCount--;
    }

    private String postID;
    private String message;
    private User author;
    private int voteCount;
}
