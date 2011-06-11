package ppl.dsl.deliszt.field

import scala.virtualization.lms.common.ScalaOpsPkg
import scala.virtualization.lms.common.{BaseExp, Base}
import ppl.dsl.deliszt.{DeLisztLift, DeLisztCompiler, DeLiszt}
import ppl.dsl.deliszt.datastruct.scala._

/**
 * author: Michael Wu (mikemwu@stanford.edu)
 * last modified: 04/26/2011
 *
 * Pervasive Parallelism Laboratory (PPL)
 * Stanford University
 */

trait FieldImplOps { this: DeLiszt =>
  def field_obj_const_impl[MO<:MeshObj:Manifest,VT:Manifest](v : Rep[VT]): Rep[Field[MO,VT]]
  def field_obj_label_impl[MO<:MeshObj:Manifest,VT:Manifest](url : Rep[String]): Rep[Field[MO,VT]]
}

trait FieldImplOpsStandard extends FieldImplOps {
  this: DeLisztCompiler with DeLisztLift =>

  //////////////////////////
  // kernel implementations
  def field_obj_const_impl[MO<:MeshObj:Manifest,VT:Manifest](c : Rep[VT]) = {
    val v = Field[MO,VT]()
    for (i <- 0 until v.size) {
      v.update(i, c)
    }
    v
  }

  def field_obj_label_impl[MO<:MeshObj:Manifest,VT:Manifest](url: Rep[String]) = {
    val v = Field[MO,VT]()
    val lf = label[MO](url)

    for (mo <- meshSet[MO]) {
      v.update(i, lf(mo))
    }
    v
  }
}