package com.mindlinksoft.recruitment.mychat;

import com.google.gson.*;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link ConversationExporter}.
 */
public class ConversationExporterTests {
    /**
     * Tests that exporting a conversation will export the conversation correctly.
     * @throws Exception When something bad happens.
     */
    @Test
    public void testExportingConversationExportsConversation() throws Exception {
        ConversationExporter exporter = new ConversationExporter();
       
        exporter.exportConversation("chat.txt", "output.json", "4", "");
//        exporter.exportConversation("chat.txt", "output.json", "3", "blacklist.txt");
//        exporter.exportConversation("chat.txt", "output.json", "1", "bob");
//        exporter.exportConversation("chat.txt", "output.json", "2", "are");

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Instant.class, new InstantDeserializer());

        Gson g = builder.create();

        Conversation c = g.fromJson(new InputStreamReader(new FileInputStream("output.json")), Conversation.class);

        assertEquals("My Conversation", c.name);

        assertEquals(7, c.messages.size());
//       assertEquals(3, c.messages.size());
//      assertEquals(2, c.messages.size());


        Message[] ms = new Message[c.messages.size()];
        c.messages.toArray(ms);
       
       UserReport[] report = new UserReport[c.report.size()];
       c.report.toArray(report);

        assertEquals(ms[0].timestamp, Instant.ofEpochSecond(1448470901));
        assertEquals(ms[0].senderId, "bob");
        assertEquals(ms[0].content, "Hello there!");

        assertEquals(ms[1].timestamp, Instant.ofEpochSecond(1448470905));
        assertEquals(ms[1].senderId, "mike");
        //assertEquals(ms[1].content, "how *redacted* you?");
        assertEquals(ms[1].content, "how are you?");


        assertEquals(ms[2].timestamp, Instant.ofEpochSecond(1448470906));
        assertEquals(ms[2].senderId, "bob");
        assertEquals(ms[2].content, "I'm good thanks, do you like pie?");

        assertEquals(ms[3].timestamp, Instant.ofEpochSecond(1448470910));
        assertEquals(ms[3].senderId, "mike");
        assertEquals(ms[3].content, "no, let me ask Angus...");

        assertEquals(ms[4].timestamp, Instant.ofEpochSecond(1448470912));
        assertEquals(ms[4].senderId, "angus");
         assertEquals(ms[4].content, "Hell yes! are we buying some pie with the card *redacted* ?");
       // assertEquals(ms[4].content, "Hell yes! *redacted* we buying some pie?");
       // assertEquals(ms[4].content, "Hell yes! are we buying some pie?");


        assertEquals(ms[5].timestamp, Instant.ofEpochSecond(1448470914));
        assertEquals(ms[5].senderId, "bob");
        assertEquals(ms[5].content, "No, just want to know if there's anybody else in the pie society...");

        assertEquals(ms[6].timestamp, Instant.ofEpochSecond(1448470915));
        assertEquals(ms[6].senderId, "angus");
        assertEquals(ms[6].content, "YES! I'm the head pie eater there...");
        
        assertEquals(report[0].userID, "bob");
        assertEquals(report[0].counter, 3);

        assertEquals(report[1].userID, "angus");
        assertEquals(report[1].counter, 2);
       

        assertEquals(report[2].userID, "mike");
        assertEquals(report[2].counter, 2);
       
    }

    class InstantDeserializer implements JsonDeserializer<Instant> {

        public Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonPrimitive()) {
                throw new JsonParseException("Expected instant represented as JSON number, but no primitive found.");
            }

            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

            if (!jsonPrimitive.isNumber()) {
                throw new JsonParseException("Expected instant represented as JSON number, but different primitive found.");
            }

            return Instant.ofEpochSecond(jsonPrimitive.getAsLong());
        }
    }
}
