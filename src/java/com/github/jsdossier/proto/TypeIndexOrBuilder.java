// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: dossier.proto

package com.github.jsdossier.proto;

public interface TypeIndexOrBuilder extends
    // @@protoc_insertion_point(interface_extends:dossier.TypeIndex)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .dossier.TypeIndex.Entry module = 1;</code>
   */
  java.util.List<com.github.jsdossier.proto.TypeIndex.Entry> 
      getModuleList();
  /**
   * <code>repeated .dossier.TypeIndex.Entry module = 1;</code>
   */
  com.github.jsdossier.proto.TypeIndex.Entry getModule(int index);
  /**
   * <code>repeated .dossier.TypeIndex.Entry module = 1;</code>
   */
  int getModuleCount();
  /**
   * <code>repeated .dossier.TypeIndex.Entry module = 1;</code>
   */
  java.util.List<? extends com.github.jsdossier.proto.TypeIndex.EntryOrBuilder> 
      getModuleOrBuilderList();
  /**
   * <code>repeated .dossier.TypeIndex.Entry module = 1;</code>
   */
  com.github.jsdossier.proto.TypeIndex.EntryOrBuilder getModuleOrBuilder(
      int index);

  /**
   * <code>repeated .dossier.TypeIndex.Entry type = 2;</code>
   */
  java.util.List<com.github.jsdossier.proto.TypeIndex.Entry> 
      getTypeList();
  /**
   * <code>repeated .dossier.TypeIndex.Entry type = 2;</code>
   */
  com.github.jsdossier.proto.TypeIndex.Entry getType(int index);
  /**
   * <code>repeated .dossier.TypeIndex.Entry type = 2;</code>
   */
  int getTypeCount();
  /**
   * <code>repeated .dossier.TypeIndex.Entry type = 2;</code>
   */
  java.util.List<? extends com.github.jsdossier.proto.TypeIndex.EntryOrBuilder> 
      getTypeOrBuilderList();
  /**
   * <code>repeated .dossier.TypeIndex.Entry type = 2;</code>
   */
  com.github.jsdossier.proto.TypeIndex.EntryOrBuilder getTypeOrBuilder(
      int index);

  /**
   * <code>repeated .dossier.Link page = 3;</code>
   */
  java.util.List<com.github.jsdossier.proto.Link> 
      getPageList();
  /**
   * <code>repeated .dossier.Link page = 3;</code>
   */
  com.github.jsdossier.proto.Link getPage(int index);
  /**
   * <code>repeated .dossier.Link page = 3;</code>
   */
  int getPageCount();
  /**
   * <code>repeated .dossier.Link page = 3;</code>
   */
  java.util.List<? extends com.github.jsdossier.proto.LinkOrBuilder> 
      getPageOrBuilderList();
  /**
   * <code>repeated .dossier.Link page = 3;</code>
   */
  com.github.jsdossier.proto.LinkOrBuilder getPageOrBuilder(
      int index);

  /**
   * <code>repeated .dossier.Link source_file = 4;</code>
   */
  java.util.List<com.github.jsdossier.proto.Link> 
      getSourceFileList();
  /**
   * <code>repeated .dossier.Link source_file = 4;</code>
   */
  com.github.jsdossier.proto.Link getSourceFile(int index);
  /**
   * <code>repeated .dossier.Link source_file = 4;</code>
   */
  int getSourceFileCount();
  /**
   * <code>repeated .dossier.Link source_file = 4;</code>
   */
  java.util.List<? extends com.github.jsdossier.proto.LinkOrBuilder> 
      getSourceFileOrBuilderList();
  /**
   * <code>repeated .dossier.Link source_file = 4;</code>
   */
  com.github.jsdossier.proto.LinkOrBuilder getSourceFileOrBuilder(
      int index);
}
