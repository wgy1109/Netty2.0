package com.huawei.insa2.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PLayer {
	public static final int maxId = 0x3b9aca00;

	protected int id;

	private AtomicInteger nextChildId = new AtomicInteger(1);

	protected PLayer parent;

	private HashMap children;

	private List listeners;
	

	protected PLayer(PLayer theParent) {
		if (theParent != null) {
			createId(theParent);
			if (theParent.children == null)
				theParent.children = new HashMap();
			theParent.children.put(new Integer(id), this);
			parent = theParent;
		}
	}
	
	public synchronized void createId(PLayer theParent){
		id = theParent.nextChildId.getAndIncrement();
		if (id >= 0x3b9aca00)
			theParent.nextChildId.set(1);
	}

	public abstract void send(PMessage pmessage) throws PException;

	public void onReceive(PMessage message) {
		int childId = getChildId(message);
		if (childId == -1) {
			PLayer child = createChild();
			child.onReceive(message);
			fireEvent(new PEvent(2, this, child));
		} else {
			PLayer child = (PLayer) children.get(new Integer(
					getChildId(message)));
			if (child == null)
				fireEvent(new PEvent(64, this, message));
			else
				child.onReceive(message);
		}
	}

	public PLayer getParent() {
		return parent;
	}

	public int getChildNumber() {
		if (children == null)
			return 0;
		else
			return children.size();
	}

	protected PLayer createChild() {
		throw new UnsupportedOperationException("Not implement");
	}

	protected int getChildId(PMessage message) {
		throw new UnsupportedOperationException("Not implement");
	}

	public void close() {
		if (parent == null) {
			throw new UnsupportedOperationException("Not implement");
		} else {
			parent.children.remove(new Integer(id));
			return;
		}
	}

	public void addEventListener(PEventListener l) {
		if (listeners == null)
			listeners = new ArrayList();
		listeners.add(l);
	}

	public void removeEventListener(PEventListener l) {
		listeners.remove(l);
	}

	protected void fireEvent(PEvent e) {
		if (listeners == null)
			return;
		for (Iterator i = listeners.iterator(); i.hasNext(); ((PEventListener) i
				.next()).handle(e))
			;
	}
}
