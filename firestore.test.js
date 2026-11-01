const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;

before(async () => {
    testEnv = await initializeTestEnvironment({
        projectId: "demo-kikiworld-project",
        firestore: {
            rules: fs.readFileSync("firestore.rules", "utf8"),
        },
    });
});

after(async () => {
    await testEnv.cleanup();
});

test("users can write their own profile", async () => {
    const alice = testEnv.authenticatedContext("alice");
    await assertSucceeds(
        alice.firestore().collection("users").doc("alice").set({ uid: "alice", username: "alice" })
    );
});

test("users cannot write others profile", async () => {
    const alice = testEnv.authenticatedContext("alice");
    await assertFails(
        alice.firestore().collection("users").doc("bob").set({ uid: "bob", username: "bob" })
    );
});
