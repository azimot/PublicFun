package edu.cs319.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import edu.cs319.dataobjects.DocumentSubSection;
import edu.cs319.dataobjects.SectionizedDocument;

public class SectionizedDocumentImpl implements SectionizedDocument {

	private String name;
	
	private List<DocumentSubSection> subSections;

	public SectionizedDocumentImpl(String name) {
		this.name = name;
		this.subSections = new ArrayList<DocumentSubSection>();
	}

	public String getName() {
		return name;
	}

	public List<DocumentSubSection> getAllSubSections() {
		return new ArrayList<DocumentSubSection>(subSections);
	}

	public void removeAllSubSections() {
		subSections.clear();
	}

	public void addAllSubSections(List<DocumentSubSection> dss) {
		subSections.addAll(dss);
	}

	public int getSubSectionCount() {
		return subSections.size();
	}

	public DocumentSubSection getSectionAt(int index) {
		return subSections.get(index);
	}

	public DocumentSubSection getSection(String id) {
		int section = getSubSectionIndex(id);
		return (section >= 0) ? subSections.get(section) : null;
	}

	public int getSubSectionIndex(String sectionID) {
		for (int i = 0; i < subSections.size(); i++) {
			if (subSections.get(i).getName().equals(sectionID)) {
				return i;
			}
		}
		return -1;
	}

	public String getFullText() {
		StringBuilder out = new StringBuilder();
		for (DocumentSubSection ds : subSections) {
			out.append(ds.getText());
			out.append("\n");
		}
		return out.toString();
	}

	public boolean addSubSection(DocumentSubSection ds, int index) {
		boolean success = false;
		if (!subSections.contains(ds) && (index >= 0) && (index <= subSections.size())) {
			subSections.add(index, ds);
			success = true;
		}
		return success;
	}

	public boolean removeSubSection(String name) {
		boolean success = false;
		int idx = getSubSectionIndex(name);
		if (idx != -1) {
			subSections.remove(idx);
			success = true;
		}
		return success;
	}

	public boolean splitSubSection(String name, String partA, String partB, int splitIndex, String userName) {
		boolean success = false;
		int index = getSubSectionIndex(name);
		if(index >= 0) {
			DocumentSubSection ds = subSections.get(index);
			if( !(ds.isLocked()) || ds.lockedByUser().equals(userName)) {
				String text = ds.getText();
				if((text.length() > splitIndex) && (splitIndex >= 0)) {
					subSections.remove(ds);
					DocumentSubSection first = new DocumentSubSectionImpl(partA);
					DocumentSubSection second = new DocumentSubSectionImpl(partB);
					first.setLocked(true, "admin");
					second.setLocked(true, "admin");
					first.setText("admin", text.substring(0, splitIndex));
					second.setText("admin", text.substring(splitIndex, text.length()));
					first.setLocked(false, "admin");
					second.setLocked(false, "admin");
					subSections.add(index, first);
					subSections.add(index + 1, second);
					if(ds.isLocked()) {
						//first.setLocked(true,ds.lockedByUser());
						second.setLocked(true,ds.lockedByUser());
					}
					success = true;
				}
			}
		}
		return success;
	}

	public boolean combineSubSections(String partA, String partB, String combinedName) {
		boolean success = false;
		int index = getSubSectionIndex(partA);
		int index2 = getSubSectionIndex(partB);
		if((index >= 0) && (index2 >= 0)) {
			DocumentSubSection first = subSections.get(index);
			DocumentSubSection second = subSections.get(index2);
			if(!(first.isLocked() || second.isLocked())) {
				subSections.remove(first);
				subSections.remove(second);
				DocumentSubSection combined = new DocumentSubSectionImpl(combinedName);
				combined.setLocked(true, "admin");
				combined.setText("admin", first.getText() + "\n" + second.getText());
				combined.setLocked(false, "admin");
				int retIndex = (index > subSections.size()) ? subSections.size() : index;
				subSections.add(retIndex, combined);
				success = true;
			}
		}
		return success;
	}

	public boolean flopSubSections(int idx1, int idx2) {
		boolean success = false;
		if((idx1 >= 0) && (idx1 < subSections.size()) && (idx2 >= 0) && (idx2 < subSections.size())) {
			DocumentSubSection ds1 = subSections.get(idx1);
			subSections.set(idx1, subSections.get(idx2));
			subSections.set(idx2, ds1);
			success = true;
		}
		return success;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof String) {
			String s = (String) o;
			return s.equals(getName());
		} else if (o instanceof SectionizedDocument) {
			SectionizedDocument sd = (SectionizedDocument) o;
			return sd.getName().equals(getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


}
