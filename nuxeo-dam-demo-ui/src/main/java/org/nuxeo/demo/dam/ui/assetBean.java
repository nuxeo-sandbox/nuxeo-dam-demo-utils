package org.nuxeo.demo.dam.ui;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.collections.core.adapter.CollectionMember;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

import java.io.Serializable;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;

@Name("assetAction")
@Scope(CONVERSATION)
public class assetBean implements Serializable{

    private static final long serialVersionUID = 1L;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    @Factory(value = "getContract", scope = EVENT)
    public DocumentModel getContract() {
        DocumentModel currentDoc = navigationContext.getCurrentDocument();
        
        try {
            CollectionMember member = currentDoc.getAdapter(CollectionMember.class);
            if (member.getCollectionIds().size()==0) return null;
            StringBuffer sb = new StringBuffer("Select * From Document Where ecm:uuid IN (");
            boolean first = true;
            for (String uid : member.getCollectionIds()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append("'"+uid+"'");
            }
            sb.append(") AND ecm:primaryType = 'IPcontract'");
            DocumentModelList collections = currentDoc.getCoreSession().query(sb.toString());
            return collections!=null && collections.size()>0 ? collections.get(0) : null;
        } catch (ClientRuntimeException e) {
            return null;
        }

    }
}
