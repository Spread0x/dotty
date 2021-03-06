package dotty.tools.dotc

import config.CompilerCommand
import core.Contexts.{Context, ContextBase}
import util.DotClass
import reporting._
import scala.util.control.NonFatal

abstract class Driver extends DotClass {

  val prompt = "\ndotc> "

  protected def newCompiler(): Compiler

  protected def emptyReporter: Reporter = new StoreReporter(null)

  protected def doCompile(compiler: Compiler, fileNames: List[String])(implicit ctx: Context): Reporter =
    if (fileNames.nonEmpty)
      try {
        val run = compiler.newRun
        run.compile(fileNames)
        run.printSummary()
      }
      catch {
        case ex: FatalError  =>
          ctx.error(ex.getMessage) // signals that we should fail compilation.
          ctx.reporter
      }
    else emptyReporter

  protected def initCtx = (new ContextBase).initialCtx

  protected def sourcesRequired = true

  def setup(args: Array[String], rootCtx: Context): (List[String], Context) = {
    val summary = CompilerCommand.distill(args)(rootCtx)
    // FIXME: We should reuse rootCtx instead of creating newCtx, but this
    // makes some tests fail with "denotation module _root_ invalid in run 2."
    val newCtx = initCtx.setCompilerCallback(rootCtx.compilerCallback)
    implicit val ctx: Context = newCtx.fresh.setSettings(summary.sstate)
    val fileNames = CompilerCommand.checkUsage(summary, sourcesRequired)
    (fileNames, ctx)
  }

  def process(args: Array[String], rootCtx: Context): Reporter = {
    val (fileNames, ctx) = setup(args, rootCtx)
    doCompile(newCompiler(), fileNames)(ctx)
  }

  def process(args: Array[String], callback: CompilerCallback): Reporter = {
    process(args, initCtx.setCompilerCallback(callback))
  }

  // We overload `process` instead of using a default argument so that we
  // can easily call this method using reflection from `RawCompiler` in sbt.
  def process(args: Array[String]): Reporter = {
    process(args, initCtx)
  }

  def main(args: Array[String]): Unit = {
    // Preload scala.util.control.NonFatal. Otherwise, when trying to catch a StackOverflowError,
    // we may try to load it but fail with another StackOverflowError and lose the original exception,
    // see <https://groups.google.com/forum/#!topic/scala-user/kte6nak-zPM>.
    val _ = NonFatal
    sys.exit(if (process(args).hasErrors) 1 else 0)
  }
}

class FatalError(msg: String) extends Exception

