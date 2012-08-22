package ppl.delite.framework.datastructures

import java.io.PrintWriter
import scala.virtualization.lms.common._
import scala.reflect.{SourceContext, RefinedManifest}
import scala.virtualization.lms.internal.{GenerationFailedException, GenericFatCodegen}
import scala.virtualization.lms.common._
import ppl.delite.framework.ops._
import ppl.delite.framework.Util._


trait DeliteArray[T] extends DeliteCollection[T] 

trait DeliteArrayOps extends StringOps {
  
  object DeliteArray {
    def apply[T:Manifest](length: Rep[Int])(implicit ctx: SourceContext) = darray_new(length)
  }

  implicit def repDArrayToDArrayOps[T:Manifest](da: Rep[DeliteArray[T]]) = new DeliteArrayOpsCls(da)

  class DeliteArrayOpsCls[T:Manifest](da: Rep[DeliteArray[T]]) {
    def length: Rep[Int] = darray_length(da)
    def apply(i: Rep[Int]): Rep[T] = darray_apply(da,i)
    def update(i: Rep[Int], x: Rep[T]): Rep[Unit] = darray_update(da,i,x)
    def map[B:Manifest](f: Rep[T] => Rep[B]) = darray_map(da,f)
    def zip[B:Manifest,R:Manifest](y: Rep[DeliteArray[B]])(f: (Rep[T],Rep[B]) => Rep[R]): Rep[DeliteArray[R]] = darray_zipwith(da,y,f)
    def reduce(f: (Rep[T],Rep[T]) => Rep[T], zero: Rep[T]): Rep[T] = darray_reduce(da,f,zero)
    def mkString(del: Rep[String]) = darray_mkstring(da,del)
    def union(rhs: Rep[DeliteArray[T]]) = darray_union(da,rhs)
    def intersect(rhs: Rep[DeliteArray[T]]) = darray_intersect(da,rhs)
    def take(n: Rep[Int]) = darray_take(da,n)
    def sort = darray_sort(da)
    def toSeq = darray_toseq(da)
  }
    
  implicit def darrayToString[A:Manifest](x: Rep[DeliteArray[A]]): Rep[String] = "[ " + repDArrayToDArrayOps(x).mkString(unit(" ")) + " ]"
  def infix_+[A:Manifest](lhs: String, rhs: Rep[DeliteArray[A]]) = string_plus(unit(lhs), darrayToString[A](rhs))
  
  def darray_new[T:Manifest](length: Rep[Int])(implicit ctx: SourceContext): Rep[DeliteArray[T]]
  def darray_length[T:Manifest](da: Rep[DeliteArray[T]])(implicit ctx: SourceContext): Rep[Int]
  def darray_apply[T:Manifest](da: Rep[DeliteArray[T]], i: Rep[Int])(implicit ctx: SourceContext): Rep[T]
  def darray_update[T:Manifest](da: Rep[DeliteArray[T]], i: Rep[Int], x: Rep[T])(implicit ctx: SourceContext): Rep[Unit]
  def darray_copy[T:Manifest](src: Rep[DeliteArray[T]], srcPos: Rep[Int], dest: Rep[DeliteArray[T]], destPos: Rep[Int], len: Rep[Int])(implicit ctx: SourceContext): Rep[Unit]
  def darray_map[A:Manifest,B:Manifest](a: Rep[DeliteArray[A]], f: Rep[A] => Rep[B]): Rep[DeliteArray[B]]    
  def darray_zipwith[A:Manifest,B:Manifest,R:Manifest](x: Rep[DeliteArray[A]], y: Rep[DeliteArray[B]], f: (Rep[A],Rep[B]) => Rep[R]): Rep[DeliteArray[R]]
  def darray_reduce[A:Manifest](x: Rep[DeliteArray[A]], f: (Rep[A],Rep[A]) => Rep[A], zero: Rep[A]): Rep[A]
  def darray_mkstring[A:Manifest](a: Rep[DeliteArray[A]], del: Rep[String]): Rep[String]
  def darray_union[A:Manifest](lhs: Rep[DeliteArray[A]], rhs: Rep[DeliteArray[A]]): Rep[DeliteArray[A]]
  def darray_intersect[A:Manifest](lhs: Rep[DeliteArray[A]], rhs: Rep[DeliteArray[A]]): Rep[DeliteArray[A]]
  def darray_take[A:Manifest](lhs: Rep[DeliteArray[A]], n: Rep[Int]): Rep[DeliteArray[A]]
  def darray_sort[A:Manifest](lhs: Rep[DeliteArray[A]]): Rep[DeliteArray[A]]
  def darray_range(st: Rep[Int], en: Rep[Int]): Rep[DeliteArray[Int]]
  def darray_toseq[A:Manifest](a: Rep[DeliteArray[A]]): Rep[Seq[A]]
}

trait DeliteArrayCompilerOps extends DeliteArrayOps {
  def darray_unsafe_update[T:Manifest](x: Rep[DeliteArray[T]], n: Rep[Int], y: Rep[T])(implicit ctx: SourceContext): Rep[Unit]
  def darray_unsafe_copy[T:Manifest](src: Rep[DeliteArray[T]], srcPos: Rep[Int], dest: Rep[DeliteArray[T]], destPos: Rep[Int], len: Rep[Int])(implicit ctx: SourceContext): Rep[Unit]
}

trait DeliteArrayOpsExp extends DeliteArrayCompilerOps with DeliteCollectionOpsExp with StructExp with EffectExp {
  this: DeliteOpsExp =>
  
  case class DeliteArrayNew[T:Manifest](length: Exp[Int]) extends DefWithManifest[T,DeliteArray[T]] 
  case class DeliteArrayLength[T:Manifest](da: Exp[DeliteArray[T]]) extends Def[Int]
  case class DeliteArrayApply[T:Manifest](da: Exp[DeliteArray[T]], i: Exp[Int]) extends DefWithManifest[T,T]
  case class DeliteArrayUpdate[T:Manifest](da: Exp[DeliteArray[T]], i: Exp[Int], x: Exp[T]) extends DefWithManifest[T,Unit]
  case class DeliteArrayCopy[T:Manifest](src: Exp[DeliteArray[T]], srcPos: Exp[Int], dest: Exp[DeliteArray[T]], destPos: Exp[Int], len: Exp[Int]) extends DefWithManifest[T,Unit]
  case class DeliteArrayMkString[T:Manifest](da: Exp[DeliteArray[T]], del: Exp[String]) extends DefWithManifest[T,String]
  case class DeliteArrayUnion[T:Manifest](lhs: Exp[DeliteArray[T]], rhs: Exp[DeliteArray[T]]) extends DefWithManifest[T,DeliteArray[T]]
  case class DeliteArrayIntersect[T:Manifest](lhs: Exp[DeliteArray[T]], rhs: Exp[DeliteArray[T]]) extends DefWithManifest[T,DeliteArray[T]]
  case class DeliteArrayTake[T:Manifest](lhs: Exp[DeliteArray[T]], n: Exp[Int]) extends DefWithManifest[T,DeliteArray[T]]
  case class DeliteArraySort[T:Manifest](da: Exp[DeliteArray[T]]) extends DefWithManifest[T,DeliteArray[T]]
  case class DeliteArrayRange(st: Exp[Int], en: Exp[Int]) extends Def[DeliteArray[Int]]
  case class DeliteArrayToSeq[A:Manifest](x: Exp[DeliteArray[A]]) extends Def[Seq[A]]
  
  //////////////////
  // delite ops
  
  case class DeliteArrayMap[A:Manifest,B:Manifest](in: Exp[DeliteArray[A]], func: Exp[A] => Exp[B])
    extends DeliteOpMap[A,B,DeliteArray[B]] {

    val size = copyTransformedOrElse(_.size)(in.length)
    override def alloc(len: Exp[Int]) = DeliteArray[B](len)
  }
  
  case class DeliteArrayZipWith[A:Manifest,B:Manifest,R:Manifest](inA: Exp[DeliteArray[A]], inB: Exp[DeliteArray[B]],
                                                                  func: (Exp[A], Exp[B]) => Exp[R])
    extends DeliteOpZipWith[A,B,R,DeliteArray[R]] {

    override def alloc(len: Exp[Int]) = DeliteArray[R](len)
    val size = copyTransformedOrElse(_.size)(inA.length)
  }
  
  case class DeliteArrayReduce[A:Manifest](in: Exp[DeliteArray[A]], func: (Exp[A], Exp[A]) => Exp[A], zero: Exp[A])
    extends DeliteOpReduce[A] {
    
    val size = copyTransformedOrElse(_.size)(in.length)    
  }  
  
  case class DeliteArrayMapFilter[A:Manifest,B:Manifest](in: Exp[DeliteArray[A]], func: Exp[A] => Exp[B], cond: Exp[A] => Exp[Boolean])
    extends DeliteOpFilter[A,B,DeliteArray[B]] {

    override def alloc(len: Exp[Int]) = DeliteArray[B](len)
    val size = copyTransformedOrElse(_.size)(in.length)
  }
  
   
  /////////////////////
  // delite collection
    
  def isDeliteArray[A](x: Exp[DeliteCollection[A]])(implicit ctx: SourceContext) = isSubtype(x.tp.erasure,classOf[DeliteArray[A]])  
  def asDeliteArray[A](x: Exp[DeliteCollection[A]])(implicit ctx: SourceContext) = x.asInstanceOf[Exp[DeliteArray[A]]]
    
  override def dc_size[A:Manifest](x: Exp[DeliteCollection[A]])(implicit ctx: SourceContext) = { 
    if (isDeliteArray(x)) asDeliteArray(x).length
    else super.dc_size(x)
  }
  
  override def dc_apply[A:Manifest](x: Exp[DeliteCollection[A]], n: Exp[Int])(implicit ctx: SourceContext) = {
    if (isDeliteArray(x)) asDeliteArray(x).apply(n)
    else {
      super.dc_apply(x,n)    
    }
  }
  
  override def dc_update[A:Manifest](x: Exp[DeliteCollection[A]], n: Exp[Int], y: Exp[A])(implicit ctx: SourceContext) = {
    if (isDeliteArray(x)) asDeliteArray(x).update(n,y)
    else super.dc_update(x,n,y)        
  }
  
  override def dc_parallelization[A:Manifest](x: Exp[DeliteCollection[A]], hasConditions: Boolean)(implicit ctx: SourceContext) = {
    if (isDeliteArray(x)) {
      if (hasConditions == true) throw new UnsupportedOperationException("DeliteArray: cannot have conditional Delite ops with a DeliteArray as input")
      ParFlat
    }
    else super.dc_parallelization(x, hasConditions)
  }
    
  def darray_new[T:Manifest](length: Exp[Int])(implicit ctx: SourceContext) = reflectMutable(DeliteArrayNew[T](length))
  def darray_length[T:Manifest](da: Exp[DeliteArray[T]])(implicit ctx: SourceContext) = reflectPure(DeliteArrayLength[T](da))
  def darray_apply[T:Manifest](da: Exp[DeliteArray[T]], i: Exp[Int])(implicit ctx: SourceContext) = reflectPure(DeliteArrayApply[T](da,i))
  def darray_update[T:Manifest](da: Exp[DeliteArray[T]], i: Exp[Int], x: Exp[T])(implicit ctx: SourceContext) = reflectWrite(da)(DeliteArrayUpdate[T](da,i,x))  
  def darray_copy[T:Manifest](src: Exp[DeliteArray[T]], srcPos: Exp[Int], dest: Exp[DeliteArray[T]], destPos: Exp[Int], len: Exp[Int])(implicit ctx: SourceContext) = reflectWrite(dest)(DeliteArrayCopy(src,srcPos,dest,destPos,len))  
  def darray_map[A:Manifest,B:Manifest](a: Exp[DeliteArray[A]], f: Exp[A] => Exp[B]) = reflectPure(DeliteArrayMap(a,f))   
  def darray_zipwith[A:Manifest,B:Manifest,R:Manifest](x: Rep[DeliteArray[A]], y: Rep[DeliteArray[B]], f: (Rep[A],Rep[B]) => Rep[R]) = reflectPure(DeliteArrayZipWith(x,y,f))
  def darray_reduce[A:Manifest](x: Exp[DeliteArray[A]], f: (Exp[A],Exp[A]) => Exp[A], zero: Exp[A]) = reflectPure(DeliteArrayReduce(x,f,zero))
  def darray_mkstring[A:Manifest](a: Exp[DeliteArray[A]], del: Exp[String]) = reflectPure(DeliteArrayMkString(a,del))
  def darray_union[A:Manifest](lhs: Exp[DeliteArray[A]], rhs: Exp[DeliteArray[A]]) = reflectPure(DeliteArrayUnion(lhs,rhs))
  def darray_intersect[A:Manifest](lhs: Exp[DeliteArray[A]], rhs: Exp[DeliteArray[A]]) = reflectPure(DeliteArrayIntersect(lhs,rhs))
  def darray_take[A:Manifest](lhs: Exp[DeliteArray[A]], n: Exp[Int]) = reflectPure(DeliteArrayTake(lhs,n))
  def darray_sort[A:Manifest](lhs: Exp[DeliteArray[A]]) = reflectPure(DeliteArraySort(lhs))
  def darray_range(st: Exp[Int], en: Exp[Int]) = reflectPure(DeliteArrayRange(st,en))
  def darray_mapfilter[A:Manifest,B:Manifest](lhs: Exp[DeliteArray[A]], map: Exp[A] => Exp[B], cond: Exp[A] => Exp[Boolean]) = reflectPure(DeliteArrayMapFilter(lhs,map,cond))
  def darray_toseq[A:Manifest](a: Exp[DeliteArray[A]]) = DeliteArrayToSeq(a)
  
  /////////////
  // internal
  
  def darray_unsafe_update[T:Manifest](x: Exp[DeliteArray[T]], n: Exp[Int], y: Exp[T])(implicit ctx: SourceContext) = DeliteArrayUpdate(x,n,y)
  def darray_unsafe_copy[T:Manifest](src: Exp[DeliteArray[T]], srcPos: Exp[Int], dest: Exp[DeliteArray[T]], destPos: Exp[Int], len: Exp[Int])(implicit ctx: SourceContext) = DeliteArrayCopy(src,srcPos,dest,destPos,len)  
  
  //////////////
  // mirroring

  override def mirror[A:Manifest](e: Def[A], f: Transformer)(implicit ctx: SourceContext): Exp[A] = {
    (e match {
      case DeliteArrayLength(a) => darray_length(f(a))
      case DeliteArrayApply(a,x) => darray_apply(f(a),f(x))
      case e@DeliteArrayTake(a,x) => darray_take(f(a),f(x))(e.mA)
      case e@DeliteArraySort(x) => darray_sort(f(x))(e.mA)
      case e@DeliteArrayCopy(a,ap,d,dp,l) => toAtom(DeliteArrayCopy(f(a),f(ap),f(d),f(dp),f(l))(e.mA))(mtype(manifest[A]),implicitly[SourceContext])
      case e@DeliteArrayMap(in,g) => reflectPure(new { override val original = Some(f,e) } with DeliteArrayMap(f(in),f(g))(e.dmA,e.dmB))(mtype(manifest[A]),implicitly[SourceContext])      
      case e@DeliteArrayReduce(in,g,z) => reflectPure(new { override val original = Some(f,e) } with DeliteArrayReduce(f(in),f(g),f(z))(e.dmA))(mtype(manifest[A]),implicitly[SourceContext])
      case Reflect(e@DeliteArrayNew(l), u, es) => reflectMirrored(Reflect(DeliteArrayNew(f(l))(e.mA), mapOver(f,u), f(es)))(mtype(manifest[A]))
      case Reflect(DeliteArrayApply(l,r), u, es) => reflectMirrored(Reflect(DeliteArrayApply(f(l),f(r)), mapOver(f,u), f(es)))(mtype(manifest[A]))
      case Reflect(DeliteArrayLength(a), u, es) => reflectMirrored(Reflect(DeliteArrayLength(f(a)), mapOver(f,u), f(es)))(mtype(manifest[A]))
      case Reflect(DeliteArrayUpdate(l,i,r), u, es) => reflectMirrored(Reflect(DeliteArrayUpdate(f(l),f(i),f(r)), mapOver(f,u), f(es)))(mtype(manifest[A]))   
      case Reflect(e@DeliteArraySort(x), u, es) => reflectMirrored(Reflect(DeliteArraySort(f(x))(e.mA), mapOver(f,u), f(es)))(mtype(manifest[A]))     
      case Reflect(e@DeliteArrayCopy(a,ap,d,dp,l), u, es) => reflectMirrored(Reflect(DeliteArrayCopy(f(a),f(ap),f(d),f(dp),f(l))(e.mA), mapOver(f,u), f(es)))(mtype(manifest[A]))     
      case Reflect(e@DeliteArrayMap(in,g), u, es) => reflectMirrored(Reflect(new { override val original = Some(f,e) } with DeliteArrayMap(f(in),f(g))(e.dmA,e.dmB), mapOver(f,u), f(es)))(mtype(manifest[A]))
      case Reflect(e@DeliteArrayReduce(in,g,z), u, es) => reflectMirrored(Reflect(new { override val original = Some(f,e) } with DeliteArrayReduce(f(in),f(g),f(z))(e.dmA), mapOver(f,u), f(es)))(mtype(manifest[A]))
      case _ => super.mirror(e,f)
    }).asInstanceOf[Exp[A]] // why??
  }
  
  /////////////////////
  // aliases and sharing
  
  override def aliasSyms(e: Any): List[Sym[Any]] = e match {
    case DeliteArrayCopy(s,sp,d,dp,l) => Nil
    case _ => super.aliasSyms(e)
  }

  override def containSyms(e: Any): List[Sym[Any]] = e match {
    case DeliteArrayCopy(s,sp,d,dp,l) => Nil
    case _ => super.containSyms(e)
  }

  override def extractSyms(e: Any): List[Sym[Any]] = e match {
    case DeliteArrayCopy(s,sp,d,dp,l) => Nil
    case _ => super.extractSyms(e)
  }

  override def copySyms(e: Any): List[Sym[Any]] = e match {
    case DeliteArrayCopy(s,sp,d,dp,l) => Nil // ??
    case _ => super.copySyms(e)
  }    
  
}

trait DeliteArrayOpsExpOpt extends DeliteArrayOpsExp with StructExpOptCommon {
  this: DeliteOpsExp =>

  case class SoaTag[T](base: StructTag[T], length: Exp[Int]) extends StructTag[DeliteArray[T]]

  //TODO: choosing the length of the first array creates an unnecessary dependency (all arrays must have same length)
  override def darray_length[T:Manifest](da: Exp[DeliteArray[T]])(implicit ctx: SourceContext) = da match {
    case Def(Loop(size,_,b:DeliteCollectElem[_,_,_])) if b.cond == Nil => size
    case Def(Struct(SoaTag(tag,len), elems:Map[String,Exp[DeliteArray[a]]])) => len    
    case _ => super.darray_length(da)
  }

  override def darray_apply[T:Manifest](da: Exp[DeliteArray[T]], i: Exp[Int])(implicit ctx: SourceContext) = da match {
    case Def(Struct(SoaTag(tag,_), elems:Map[String,Exp[DeliteArray[a]]])) =>
      struct[T](tag, elems.map(p=>(p._1, darray_apply(p._2,i)(argManifest(p._2.tp),ctx))))
    case _ => super.darray_apply(da, i)
  }

  override def darray_update[T:Manifest](da: Exp[DeliteArray[T]], i: Exp[Int], x: Exp[T])(implicit ctx: SourceContext) = da match {
    case Def(Struct(SoaTag(tag,_), elems:Map[String,Exp[DeliteArray[a]]])) =>
      elems.foreach(p=>darray_update(p._2,i,field[a](x.asInstanceOf[Exp[Record]],p._1)(argManifest(p._2.tp),ctx))(argManifest(p._2.tp),ctx))
    case _ => super.darray_update(da, i, x)
  }

  private def argManifest[A,B](m: Manifest[A]): Manifest[B] = m.typeArguments(0).asInstanceOf[Manifest[B]]

  //forwarder to appease type-checker
  private def dnew[T:Manifest](length: Exp[Int])(implicit ctx: SourceContext): Rep[DeliteArray[T]] = darray_new(length)

  //TODO: if T <: Record, but no RefinedManifest -- how do we map the fields? reflection?
  override def darray_new[T:Manifest](length: Exp[Int])(implicit ctx: SourceContext) = manifest[T] match {
    case rm: RefinedManifest[T] => struct[DeliteArray[T]](SoaTag(AnonTag(rm),length), rm.fields.map(p=>(p._1,dnew(length)(p._2,ctx))):_*)
    case _ => super.darray_new(length)
  }

  //HACK: to repackage struct returned from single task
  private def darrayManifest(arg: Manifest[_]) = new Manifest[DeliteArray[_]] {
    val erasure = classOf[DeliteArray[_]]
    override val typeArguments = List(arg)
  }

  def deliteArrayPure[T:Manifest](da: Exp[DeliteArray[T]], elems: RefinedManifest[T])(implicit ctx: SourceContext): Exp[DeliteArray[T]] = {
    struct[DeliteArray[T]](SoaTag(AnonTag(elems), da.length), elems.fields.map(e=>(e._1, field[DeliteArray[_]](da.asInstanceOf[Rep[Record]],e._1)(darrayManifest(e._2),ctx))):_*)
  }

}

trait DeliteArrayFatExp extends DeliteArrayOpsExpOpt with StructFatExpOptCommon {
  this: DeliteOpsExp =>
}

trait BaseGenDeliteArrayOps extends GenericFatCodegen {
  val IR: DeliteArrayFatExp with DeliteOpsExp
  import IR._
  
  override def unapplySimpleIndex(e: Def[Any]): Option[(Exp[Any], Exp[Int])] = e match {
    case DeliteArrayApply(da, idx) => Some((da,idx))
    case _ => super.unapplySimpleIndex(e)
  }

  override def unapplySimpleDomain(e: Def[Int]): Option[Exp[Any]] = e match {
    //case DeliteArrayLength(da) => Some(da)
    case DeliteArrayLength(a @ Def(Loop(_,_,_:DeliteCollectElem[_,_,_]))) => Some(a) // exclude hash collect (?)
    case _ => super.unapplySimpleDomain(e)
  }

}

trait ScalaGenDeliteArrayOps extends BaseGenDeliteArrayOps with ScalaGenFat {
  val IR: DeliteArrayFatExp with DeliteOpsExp
  import IR._
  
  override def emitNode(sym: Sym[Any], rhs: Def[Any]) = rhs match {
    case a@DeliteArrayNew(n) => emitValDef(sym, "new Array[" + remap(a.mA) + "](" + quote(n) + ")")
    case DeliteArrayLength(da) =>
      emitValDef(sym, quote(da) + ".length")
    case DeliteArrayApply(da, idx) =>
      emitValDef(sym, quote(da) + "(" + quote(idx) + ")")
    case DeliteArrayUpdate(da, idx, x) =>
      emitValDef(sym, quote(da) + "(" + quote(idx) + ") = " + quote(x))
    case DeliteArrayCopy(src,srcPos,dest,destPos,len) =>
      emitValDef(sym, "System.arraycopy(" + quote(src) + "," + quote(srcPos) + "," + quote(dest) + "," + quote(destPos) + "," + quote(len) + ")")
    case DeliteArrayMkString(da,x) =>
      emitValDef(sym, quote(da) + ".mkString(" + quote(x) + ")")
    case DeliteArrayUnion(lhs,rhs) =>
      emitValDef(sym, quote(lhs) + " union " + quote(rhs))
    case DeliteArrayIntersect(lhs,rhs) =>
      emitValDef(sym, quote(lhs) + " intersect " + quote(rhs))    
    case DeliteArrayTake(lhs,n) =>
      emitValDef(sym, quote(lhs) + ".take(" + quote(n) + ")")
    case a@DeliteArraySort(x) => 
      stream.println("val " + quote(sym) + " = {")
      stream.println("val d = new Array[" + remap(a.mA) + "](" + quote(x) + ".length" + ")")
      stream.println("System.arraycopy(" + quote(x) + ", 0, d, 0, " + quote(x) + ".length)")
      stream.println("scala.util.Sorting.quickSort(d)")
      stream.println("d")
      stream.println("}")    
    case DeliteArrayRange(st,en) =>
      emitValDef(sym, "Array.range(" + quote(st) + "," + quote(en) + ")")
    case DeliteArrayToSeq(a) => emitValDef(sym, quote(a) + ".toSeq")
    case _ => super.emitNode(sym, rhs)
  }

  override def remap[A](m: Manifest[A]): String = m.erasure.getSimpleName match {
    case "DeliteArray" => m.typeArguments(0) match {
      case s if s <:< manifest[Record] =>
        structName(m)
      case arg => "Array[" + remap(arg) + "]"
    }
    case _ => super.remap(m)
  }

}


trait CudaGenDeliteArrayOps extends BaseGenDeliteArrayOps with CudaGenFat {
  val IR: DeliteArrayFatExp with DeliteOpsExp
  import IR._

  override def emitNode(sym: Sym[Any], rhs: Def[Any]) = rhs match {
    //case DeliteArrayNew(length) =>
    //  emitValDef(sym, "new Array[" + remap(sym.tp.tpArguments(0)) + "](" + quote(length) + ")")
    case DeliteArrayLength(da) =>
      emitValDef(sym, quote(da) + ".length")
    case DeliteArrayApply(da, idx) =>
      emitValDef(sym, quote(da) + ".apply(" + quote(idx) + ")")
    case DeliteArrayUpdate(da, idx, x) =>
      emitValDef(sym, quote(da) + ".update(" + quote(idx) + "," + quote(x) + ");")
    case _ => super.emitNode(sym, rhs)
  }

  override def remap[A](m: Manifest[A]): String = m.erasure.getSimpleName match {
    case "DeliteArray" => m.typeArguments(0) match {
      case s if s <:< manifest[Record] =>
        throw new GenerationFailedException("CudaGen: Struct generation not possible")
      case arg => "DeliteArray<" + remap(arg) + ">"
    }
    case _ => super.remap(m)
  }

  override def isObjectType[A](m: Manifest[A]) : Boolean = m.erasure.getSimpleName match {
      case "DeliteArray" => m.typeArguments(0) match {
        case s if s <:< manifest[Record] => false
        case arg => true //if isPrimitiveType(arg) => true //Currently only allow primitive type arrays
      }
      case _ => super.isObjectType(m)
  }

}

trait OpenCLGenDeliteArrayOps extends BaseGenDeliteArrayOps with OpenCLGenFat {
  val IR: DeliteArrayFatExp with DeliteOpsExp
  import IR._

  override def emitNode(sym: Sym[Any], rhs: Def[Any]) = rhs match {
    //case DeliteArrayNew(length) =>
    //  emitValDef(sym, "new Array[" + remap(sym.tp.typeArguments(0)) + "](" + quote(length) + ")")
    case DeliteArrayLength(da) =>
      emitValDef(sym, remap(da.tp) + "_size(" + quote(da) + ")")
    case DeliteArrayApply(da, idx) =>
      emitValDef(sym, remap(da.tp) + "_apply(" + quote(da) + "," + quote(idx) + ")")
    case DeliteArrayUpdate(da, idx, x) =>
      emitValDef(sym, remap(da.tp) + "_update(" + quote(da) + "," + quote(idx) + "," + quote(x) + ")")
    case _ => super.emitNode(sym, rhs)
  }

  override def remap[A](m: Manifest[A]): String = m.erasure.getSimpleName match {
    case "DeliteArray" => m.typeArguments(0) match {
      case s if s <:< manifest[Record] =>
        throw new GenerationFailedException("OpenCLGen: Struct generation not possible")
      case arg => "DeliteArray_" + remap(arg)
    }
    case _ => super.remap(m)
  }

  override def isObjectType[A](m: Manifest[A]) : Boolean = m.erasure.getSimpleName match {
      case "DeliteArray" => m.typeArguments(0) match {
        case s if s <:< manifest[Record] => false
        case arg => true //if isPrimitiveType(arg) => true //Currently only allow primitive type arrays
      }
      case _ => super.isObjectType(m)
  }

}
