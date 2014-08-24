/*
 * Copyright (C) 2014 Miroslav Cupak (mirocupak@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
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
