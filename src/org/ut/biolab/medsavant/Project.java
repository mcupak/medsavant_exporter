/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant;

import java.io.Serializable;

/**
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    private String project;
    private String referenceGenome;

    public Project() {
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getReferenceGenome() {
        return referenceGenome;
    }

    public void setReferenceGenome(String referenceGenome) {
        this.referenceGenome = referenceGenome;
    }

    @Override
    public String toString() {
        return "Project{" + "project=" + project + ", referenceGenome=" + referenceGenome + '}';
    }

}
