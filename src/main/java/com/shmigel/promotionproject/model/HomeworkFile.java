package com.shmigel.promotionproject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "homework_files")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkFile {

    @Id
    @GeneratedValue(generator = "homework_files_id_seq")
    private Long id;

    private String fileName;

    private String fileText;

    private String fileChecksum;

    @OneToOne(fetch = FetchType.LAZY)
    private Homework homework;

    public HomeworkFile(String fileName, String fileText, String fileChecksum) {
        this.fileName = fileName;
        this.fileText = fileText;
        this.fileChecksum = fileChecksum;
    }
}
