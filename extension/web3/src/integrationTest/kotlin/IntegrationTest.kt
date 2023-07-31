import io.hamal.extension.web3.Web3ExtensionFactory
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name

object IntegrationTest {
    @TestFactory
    fun run(): List<DynamicTest> {
        NativeLoader.load(Resources)
        return collectFiles().map { testFile ->
            dynamicTest("${testFile.parent.name}/${testFile.name}") {
                val luaCode = String(Files.readAllBytes(testFile))
                Sandbox().use { sb ->
                    sb.register(Web3ExtensionFactory().create())
                    sb.load(luaCode)
                }
            }
        }.toList()
    }
}

private val testPath = Paths.get("src", "integrationTest", "resources", "suite")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }