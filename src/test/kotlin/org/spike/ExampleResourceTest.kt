package org.spike

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.spike.models.OrgModel

@QuarkusTest
class ExampleResourceTest {

    @Test
    fun testListOrgsEndpoint() {

        val orgName = RandomStringUtils.random(5)
        val payload = """
            {
                "name" : "$orgName"
            }
        """.trimIndent()

        val createdOrg = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .`when`().post("/orgs/create")
                .then().contentType(ContentType.JSON).extract().`as`(OrgModel::class.java)

        val orgs = given()
            .`when`().get("/orgs/list")
            .then()
            .statusCode(200)
            .extract().`as`(Array<OrgModel>::class.java)

        Assertions.assertNotNull { orgs.find { it.uid == createdOrg.uid } }
    }

    @Test
    fun testCreateOrgEndpoint() {
        val orgName = RandomStringUtils.random(5)
        val payload = """
            {
                "name" : "$orgName"
            }
        """.trimIndent()

        val createdOrg = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .`when`().post("/orgs/create")
                .then()
                .statusCode(200)
                .extract().`as`(OrgModel::class.java)

        Assertions.assertTrue { createdOrg.name == orgName }
    }

}