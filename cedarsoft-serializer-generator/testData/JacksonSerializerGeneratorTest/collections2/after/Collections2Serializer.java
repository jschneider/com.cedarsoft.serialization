import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class Collections2Serializer extends com.cedarsoft.serialization.jackson.AbstractJacksonSerializer<Collections2> {
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_ROLES = "roles";
    public static final String PROPERTY_EMAILS = "emails";
    public static final String PROPERTY_USER_DETAILS = "userDetails";
    public static final String PROPERTY_SINGLE_EMAIL = "singleEmail";

    @javax.inject.Inject
    public Collections2Serializer(@NotNull StringSerializer stringSerializer, @NotNull RoleSerializer roleSerializer, @NotNull EmailSerializer emailSerializer, @NotNull UserDetailsSerializer userDetailsSerializer) {
        super("collections-2", com.cedarsoft.version.VersionRange.from(1, 0, 0).to());
        getDelegatesMappings().add(stringSerializer).responsibleFor(String.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(roleSerializer).responsibleFor(Role.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(emailSerializer).responsibleFor(Email.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        getDelegatesMappings().add(userDetailsSerializer).responsibleFor(UserDetails.class).map(1, 0, 0).toDelegateVersion(1, 0, 0);
        assert getDelegatesMappings().verify();
    }

    @Override
    public void serialize(@NotNull com.fasterxml.jackson.core.JsonGenerator serializeTo, @NotNull Collections2 object, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, com.fasterxml.jackson.core.JsonProcessingException {
        verifyVersionWritable(formatVersion);
        serialize(object.getName(), String.class, PROPERTY_NAME, serializeTo, formatVersion);
        serializeArray(object.getRoles(), Role.class, PROPERTY_ROLES, serializeTo, formatVersion);
        serializeArray(object.getEmails(), Email.class, PROPERTY_EMAILS, serializeTo, formatVersion);
        serialize(object.getUserDetails(), UserDetails.class, PROPERTY_USER_DETAILS, serializeTo, formatVersion);
        serialize(object.getSingleEmail(), Email.class, PROPERTY_SINGLE_EMAIL, serializeTo, formatVersion);
    }

    @Override
    @NotNull
    public Collections2 deserialize(@NotNull com.fasterxml.jackson.core.JsonParser deserializeFrom, @NotNull com.cedarsoft.version.Version formatVersion) throws IOException, com.cedarsoft.version.VersionException, com.fasterxml.jackson.core.JsonProcessingException {
        verifyVersionWritable(formatVersion);

        String name = null;
        List<? extends Role> roles = null;
        List<? extends Email> emails = null;
        UserDetails userDetails = null;
        Email singleEmail = null;

        com.cedarsoft.serialization.jackson.JacksonParserWrapper parser = new com.cedarsoft.serialization.jackson.JacksonParserWrapper(deserializeFrom);
        while (parser.nextToken() == com.fasterxml.jackson.core.JsonToken.FIELD_NAME) {
            String currentName = parser.getCurrentName();

            if (currentName.equals(PROPERTY_NAME)) {
                parser.nextToken();
                name = deserialize(String.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_ROLES)) {
                parser.nextToken();
                roles = deserializeArray(Role.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_EMAILS)) {
                parser.nextToken();
                emails = deserializeArray(Email.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_USER_DETAILS)) {
                parser.nextToken();
                userDetails = deserialize(UserDetails.class, formatVersion, deserializeFrom);
                continue;
            }
            if (currentName.equals(PROPERTY_SINGLE_EMAIL)) {
                parser.nextToken();
                singleEmail = deserialize(Email.class, formatVersion, deserializeFrom);
                continue;
            }
            throw new IllegalStateException("Unexpected field reached <" + currentName + ">");
        }

        parser.verifyDeserialized(name, PROPERTY_NAME);
        assert name != null;
        parser.verifyDeserialized(roles, PROPERTY_ROLES);
        assert roles != null;
        parser.verifyDeserialized(emails, PROPERTY_EMAILS);
        assert emails != null;
        parser.verifyDeserialized(userDetails, PROPERTY_USER_DETAILS);
        assert userDetails != null;
        parser.verifyDeserialized(singleEmail, PROPERTY_SINGLE_EMAIL);
        assert singleEmail != null;

        parser.ensureObjectClosed();

        Collections2 object = new Collections2(name, singleEmail, userDetails);
        object.setRoles(roles);
        object.setEmails(emails);
        return object;
    }
}