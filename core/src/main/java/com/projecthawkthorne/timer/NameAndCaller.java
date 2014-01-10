package com.projecthawkthorne.timer;

class NameAndCaller {

	private String name;
	private Timeable caller;

	public NameAndCaller(String name, Timeable caller) {
		this.setName(name);
		this.setCaller(caller);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getCaller() == null) ? 0 : getCaller().hashCode());
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NameAndCaller other = (NameAndCaller) obj;
		if (getCaller() == null) {
			if (other.getCaller() != null)
				return false;
		} else if (!getCaller().equals(other.getCaller()))
			return false;
		if (getName() != other.getName())
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timeable getCaller() {
		return caller;
	}

	public void setCaller(Timeable caller) {
		this.caller = caller;
	}

}
