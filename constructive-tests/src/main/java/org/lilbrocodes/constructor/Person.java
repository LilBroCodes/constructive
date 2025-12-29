package org.lilbrocodes.constructor;

import org.lilbrocodes.constructive.api.v1.anno.Constructive;
import org.lilbrocodes.constructive.api.v1.anno.Target;
import org.lilbrocodes.constructive.api.v1.anno.builder.*;

/**
 * Example class to test all builder annotations.
 */
@Constructive(builder = true)
@Target(builder = "CustomPersonBuilder", builderPackage = "org.lilbrocodes.constructor.builder")
public class Person {
    @Default
    @Description("The full legal name for the person")
    @Name(name = "name")
    @NullCheck(check = "%f.isBlank()")
    private String fullLegalName = "Anonymous";

    @Required
    @Description("The unique identifier for the person")
    private final String id;

    @Required(required = false)
    @Description("The nickname of the person, optional")
    private final String nickname;

    @Default(method = "defaultAge")
    @Description("The age of the person, defaults to 18")
    private final int age;

    @Default
    private String country = "Unknown";

    @Composite
    private final Address address;

    @Transient
    private String internalCode;

    @HardRequire(require = true, nullable = false)
    private final String email;

    @Builder
    private final java.util.List<String> tags;

    public Person(String name, String id, String nickname, int age, String country, Address address, String email, java.util.List<String> tags) {
        this.fullLegalName = name;
        this.id = id;
        this.nickname = nickname;
        this.age = age;
        this.country = country;
        this.address = address;
        this.email = email;
        this.tags = tags;
    }

    public static int defaultAge() {
        return 18;
    }

    @Constructive(builder = true)
    @Target(builder = "CustomAddressBuilder")
    public static class Address {
        @HardRequire
        private final String street;

        @HardRequire
        private final String city;

        @Required(required = false)
        private final String zipCode;

        @Builder
        private java.util.List<String> residents;

        public Address(String street, String city, String zipCode, java.util.List<String> residents) {
            this.street = street;
            this.city = city;
            this.zipCode = zipCode;
            this.residents = residents;
        }
    }
}
