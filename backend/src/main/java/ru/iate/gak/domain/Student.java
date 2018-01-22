package ru.iate.gak.domain;

import ru.iate.gak.model.StudentEntity;

public class Student extends LongIdentifiable {
    private String firstname;
    private String middlename;
    private String lastname;
    private String title;
    private Group group;
    private User mentor;
    private User reviewer;
    private byte[] report;
    private byte[] presentation;

    public Student() {}

    public Student(StudentEntity studentEntity) {
        super(studentEntity.getId());
        this.firstname = studentEntity.getFirstname();
        this.middlename = studentEntity.getMiddlename();
        this.lastname = studentEntity.getLastname();
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getMentor() {
        return mentor;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }


    public byte[] getReport() {
        return report;
    }

    public void setReport(byte[] report) {
        this.report = report;
    }

    public byte[] getPresentation() {
        return presentation;
    }

    public void setPresentation(byte[] presentation) {
        this.presentation = presentation;
    }
}