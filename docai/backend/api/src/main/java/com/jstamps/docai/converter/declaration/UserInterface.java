package com.jstamps.docai.converter.declaration;

public interface UserInterface<Entity, Dto, FormDto> {

    public Dto editUser(FormDto object);
}
