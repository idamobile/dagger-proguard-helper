package com.idamobile.dagger.helper;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class TypeParams {
    private String typeName;
    private List<TypeParams> genericTypes = new ArrayList<TypeParams>();

    public TypeParams(TypeMirror type) {
        typeName = type.toString();
        if (typeName.contains("<")) {
            typeName = typeName.substring(0, typeName.indexOf("<"));
        }

        if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) type;
            if (!declaredType.getTypeArguments().isEmpty()) {
                for (TypeMirror genericMirrorType : declaredType.getTypeArguments()) {
                    genericTypes.add(new TypeParams(genericMirrorType));
                }
            }
        }
    }

    public String getName() {
        return typeName;
    }

    public List<TypeParams> getGenerics(){
        return genericTypes;
    }

    public boolean isPrimitiveType() {
        return !typeName.contains(".");
    }

    public boolean isList() {
        return typeName.startsWith("java.util.List");
    }

    public boolean isByteArray() {
        return typeName.equals("byte[]");
    }

    public boolean isKeepRequaried() {
        return !isPrimitiveType() && !isSimpleTypeWrapper() && !typeName.equals("java.lang.String");
    }

    private boolean isSimpleTypeWrapper() {
        if (typeName.equals("java.lang.Byte")) {
            return true;
        }
        if (typeName.equals("java.lang.Short")) {
            return true;
        }
        if (typeName.equals("java.lang.Integer")) {
            return true;
        }
        if (typeName.equals("java.lang.Long")) {
            return true;
        }
        if (typeName.equals("java.lang.Float")) {
            return true;
        }
        if (typeName.equals("java.lang.Double")) {
            return true;
        }
        if (typeName.equals("java.lang.Char")) {
            return true;
        }
        if (typeName.equals("java.lang.Boolean")) {
            return true;
        }
        return false;
    }

}
