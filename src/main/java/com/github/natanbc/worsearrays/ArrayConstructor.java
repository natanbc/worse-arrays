package com.github.natanbc.worsearrays;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

class ArrayConstructor {
    @SuppressWarnings("unchecked")
    static <T> Array<T> create(int size) {
        try {
            return ArrayConstructor.<T>classFor(size).newInstance();
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<Array<T>> classFor(int size) {
        String resultName = size + "SizedArray";
        try {
            return (Class<Array<T>>)Definer.INSTANCE.loadClass(resultName);
        } catch(ClassNotFoundException e) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            String internalName = resultName.replace('.', '/');
            cw.visit(V1_8, ACC_PUBLIC | ACC_FINAL, internalName, null, "java/lang/Object", new String[]{
                    Array.class.getName().replace('.', '/')
            });
            for(int i = 0; i < size; i++) {
                cw.visitField(ACC_PRIVATE, String.valueOf(i), "Ljava/lang/Object;", null, null);
            }
            MethodVisitor mv;

            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            {
                mv = cw.visitMethod(ACC_PUBLIC, "size", "()I", null, null);
                mv.visitCode();
                mv.visitLdcInsn(size);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            label: {
                mv = cw.visitMethod(ACC_PUBLIC, "get", "(I)Ljava/lang/Object;", null, null);
                mv.visitCode();
                if(size == 0) {
                    throwOOB(mv);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                    break label;
                }
                Label[] labels = new Label[size];
                Label dflt = new Label();
                for(int i = 0; i < size; i++) {
                    labels[i] = new Label();
                }
                mv.visitVarInsn(ILOAD, 1);
                mv.visitTableSwitchInsn(0, size - 1, dflt, labels);
                Label ret = new Label();
                for(int i = 0; i < size; i++) {
                    mv.visitLabel(labels[i]);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, internalName, String.valueOf(i), "Ljava/lang/Object;");
                    mv.visitJumpInsn(GOTO, ret);
                }
                mv.visitLabel(dflt);
                throwOOB(mv);
                mv.visitLabel(ret);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            label: {
                mv = cw.visitMethod(ACC_PUBLIC, "set", "(ILjava/lang/Object;)V", null, null);
                mv.visitCode();
                if(size == 0) {
                    throwOOB(mv);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                    break label;
                }
                Label[] labels = new Label[size];
                Label dflt = new Label();
                for(int i = 0; i < size; i++) {
                    labels[i] = new Label();
                }
                mv.visitVarInsn(ILOAD, 1);
                mv.visitTableSwitchInsn(0, size - 1, dflt, labels);
                Label ret = new Label();
                for(int i = 0; i < size; i++) {
                    mv.visitLabel(labels[i]);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitFieldInsn(PUTFIELD, internalName, String.valueOf(i), "Ljava/lang/Object;");
                    mv.visitJumpInsn(GOTO, ret);
                }
                mv.visitLabel(dflt);
                throwOOB(mv);
                mv.visitLabel(ret);
                mv.visitInsn(RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            return (Class<Array<T>>)Definer.define(cw.toByteArray());
        }
    }

    private static void throwOOB(MethodVisitor mv) {
        String exType = Type.getInternalName(ArrayIndexOutOfBoundsException.class);
        mv.visitTypeInsn(NEW, exType);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, exType, "<init>", "(I)V", false);
        mv.visitInsn(ATHROW);
    }

    private static class Definer extends ClassLoader {
        static final Definer INSTANCE = new Definer();

        Definer() {
            super(Definer.class.getClassLoader());
        }

        static Class<?> define(byte[] bytes) {
            return INSTANCE.defineClass(null, bytes, 0, bytes.length);
        }
    }
}
