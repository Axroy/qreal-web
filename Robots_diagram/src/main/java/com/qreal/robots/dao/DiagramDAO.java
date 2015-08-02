package com.qreal.robots.dao;

import com.qreal.robots.model.diagram.Diagram;
import com.qreal.robots.model.diagram.DiagramRequest;
import com.qreal.robots.model.diagram.Folder;

import java.util.List;

/**
 * Created by vladzx on 22.06.15.
 */
public interface DiagramDAO {

    public Long saveDiagram(Diagram diagram);

    public Diagram openDiagram(DiagramRequest request);

    public void rewriteDiagram(Diagram diagram);

    public Long createFolder(Folder folder);

    public Folder getFolderTree(String userName);
}
