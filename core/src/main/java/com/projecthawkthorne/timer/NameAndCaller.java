package com.projecthawkthorne.timer;

class NameAndCaller {

	final String name;
	final Timeable caller;

	public NameAndCaller(String name, Timeable caller) {
		this.name = name;
		this.caller = caller;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caller == null) ? 0 : caller.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (caller == null) {
			if (other.caller != null)
				return false;
		} else if (!caller.equals(other.caller))
			return false;
		if (name != other.name)
			return false;
		return true;
	}

}
