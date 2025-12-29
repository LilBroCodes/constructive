package org.lilbrocodes.constructive.internal.builder.model;

import javax.lang.model.type.TypeKind;
import java.util.List;

public sealed interface TypeModel {
    record Primitive(String name, Kind kind) implements TypeModel {
        @Override
        public boolean nullable() {
            return false;
        }

        @Override
        public Kind kind() {
            return kind;
        }
    }

    record Array(TypeModel component, Kind kind) implements TypeModel {
        @Override
        public boolean nullable() {
            return true;
        }

        @Override
        public Kind kind() {
            return Kind.OTHER;
        }
    }

    record Declared(String qualified, String simple, List<TypeModel> generics, Kind kind) implements TypeModel {
        @Override
        public boolean nullable() {
            return true;
        }

        @Override
        public Kind kind() {
            return kind;
        }
    }

    default Primitive primitive() {
        return (Primitive) this;
    }
    default Array array() {
        return (Array) this;
    }
    default Declared declared() {
        return (Declared) this;
    }
    boolean nullable();
    Kind kind();

    enum Kind {
        BOOLEAN,
        NUM,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR,
        OTHER;

        public static Kind map(TypeKind kind) {
            return switch (kind) {
                case BOOLEAN -> BOOLEAN;
                case BYTE, SHORT, INT -> NUM;
                case LONG -> LONG;
                case FLOAT -> FLOAT;
                case DOUBLE -> DOUBLE;
                case CHAR -> CHAR;
                default -> OTHER;
            };
        }
    }
}
