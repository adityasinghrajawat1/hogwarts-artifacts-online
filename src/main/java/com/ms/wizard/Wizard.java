package com.ms.wizard;

import com.ms.artifact.Artifact;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wizard implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE},mappedBy = "owner")
    private List<Artifact> artifacts = new ArrayList<>();

    public Wizard() {}

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public List<Artifact> getArtifacts() {return artifacts;}
    public void setArtifacts(List<Artifact> artifacts) {this.artifacts = artifacts;}

    public void addArtifact(Artifact artifact)
    {
        artifact.setOwner(this);
        this.artifacts.add(artifact);
    }

    public Integer getNumberOfArtifacts()
    {
        return artifacts.size();
    }

    public void removeAllArtifacts()
    {
        artifacts.stream().forEach(artifact -> artifact.setOwner(null));
        this.artifacts=null;
    }

    public void removeArtifact(Artifact artifactToBeAssigned)
    {
        // remove artifact owner
        artifactToBeAssigned.setOwner(null);
        artifacts.remove(artifactToBeAssigned);
    }
}
