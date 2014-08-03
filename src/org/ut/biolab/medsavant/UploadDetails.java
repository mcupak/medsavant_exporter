package org.ut.biolab.medsavant;

import java.io.Serializable;

/**
 * Upload operation configuration.
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
public class UploadDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    private int project;
    private int refId;
    private boolean includeHomoRef;
    private boolean autoPublish;
    private boolean preAnnotateWithJannovar;

    public UploadDetails() {
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }

    public boolean isIncludeHomoRef() {
        return includeHomoRef;
    }

    public void setIncludeHomoRef(boolean includeHomoRef) {
        this.includeHomoRef = includeHomoRef;
    }

    public boolean isAutoPublish() {
        return autoPublish;
    }

    public void setAutoPublish(boolean autoPublish) {
        this.autoPublish = autoPublish;
    }

    public boolean isPreAnnotateWithJannovar() {
        return preAnnotateWithJannovar;
    }

    public void setPreAnnotateWithJannovar(boolean preAnnotateWithJannovar) {
        this.preAnnotateWithJannovar = preAnnotateWithJannovar;
    }

    @Override
    public String toString() {
        return "UploadDetails{" + "project=" + project + ", refId=" + refId + ", includeHomoRef=" + includeHomoRef + ", autoPublish=" + autoPublish + ", preAnnotateWithJannovar=" + preAnnotateWithJannovar + '}';
    }

}
