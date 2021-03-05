package com.apurebase.kgraphql.integration

import com.apurebase.kgraphql.assertNoErrors
import com.apurebase.kgraphql.extract
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


class EnumTest : BaseSchemaTest() {

    @Test
    fun `query with enum field`(){
        val map = execute("{film{type}}")
        assertNoErrors(map)
        assertThat(map.extract<String>("data/film/type"), equalTo("FULL_LENGTH"))
    }

    @Test
    fun `query with enum argument`(){
        val map = execute("{ films: filmsByType(type: FULL_LENGTH){title, type}}")
        assertNoErrors(map)
        assertThat(map.extract<String>("data/films[0]/type"), equalTo("FULL_LENGTH"))
        assertThat(map.extract<String>("data/films[1]/type"), equalTo("FULL_LENGTH"))
    }

    @Test
    fun `query with enum array variables`() {
        val variables = "{\"types\":[\"FULL_LENGTH\"]}"
        val map = execute("query Search(${'$'}types: [FilmType!]!) { films: filmsByTypes(types: ${'$'}types){title, type}}", variables)
        assertNoErrors(map)
        assertThat(map.extract<String>("data/films[0]/type"), equalTo("FULL_LENGTH"))
        assertThat(map.extract<String>("data/films[1]/type"), equalTo("FULL_LENGTH"))
    }
}
