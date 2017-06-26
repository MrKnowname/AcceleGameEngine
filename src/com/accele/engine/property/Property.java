package com.accele.engine.property;

import java.util.Optional;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Indexable;

public class Property implements Indexable {

	protected final Engine engine;
	protected final String registryID;
	protected final String localizedID;
	protected Object value;
	protected final boolean typeImmutable;
	protected final boolean valueImmutable;
	protected Optional<Operation> operation;
	protected OperationLocation location;
	
	public Property(Engine engine, String registryID, String localizedID, Object value, boolean typeImmutable, boolean valueImmutable, Optional<Operation> operation, OperationLocation location) {
		this.engine = engine;
		this.registryID = registryID;
		this.localizedID = localizedID;
		this.value = value;
		this.typeImmutable = typeImmutable;
		this.valueImmutable = valueImmutable;
		this.operation = operation;
		this.location = location;
		
		if (location == OperationLocation.RUN_ON_INIT && operation.isPresent())
			operation.get().run(engine, value);
	}
	
	public Property(Engine engine, String registryID, String localizedID, Object value) {
		this(engine, registryID, localizedID, value, false, false, Optional.empty(), OperationLocation.DO_NOT_RUN);
	}
	
	@Override
	public final String getRegistryID() {
		return registryID;
	}
	
	@Override
	public final String getLocalizedID() {
		return localizedID;
	}
	
	public Object get() {
		if (operation.isPresent() && (location == OperationLocation.RUN_ON_GET || location == OperationLocation.RUN_ON_ACCESS))
			operation.get().run(engine, value);
		
		return value;
	}
	
	public void set(Object value) {
		if (operation.isPresent() && (location == OperationLocation.RUN_ON_SET || location == OperationLocation.RUN_ON_ACCESS))
			operation.get().run(engine, value);
		
		if (!valueImmutable && (!typeImmutable || (typeImmutable && value.getClass().equals(this.value.getClass()))))
			this.value = value;
	}
	
	public final boolean isTypeImmutable() {
		return typeImmutable;
	}
	
	public final boolean isValueImmutable() {
		return valueImmutable;
	}
	
	public void run() {
		if (operation.isPresent())
			operation.get().run(engine, value);
	}
	
	public boolean hasOperation() {
		return operation.isPresent();
	}
	
	public OperationLocation getOperationLocation() {
		return location;
	}
	
}
