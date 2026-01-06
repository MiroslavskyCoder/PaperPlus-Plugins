"use client";

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from "@/components/ui/button";
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { SerializedEditorState } from "lexical" 
import { Editor } from "@/components/blocks/editor-00/editor";

interface ConfigTabProps {
  runCommand: (cmd: string) => Promise<void>;
}

const initialValue = {
  root: {
    children: [
      {
        children: [
          {
            detail: 0,
            format: 0,
            mode: "normal",
            style: "",
            text: "Hello World 🚀",
            type: "text",
            version: 1,
          },
        ],
        direction: "ltr",
        format: "",
        indent: 0,
        type: "paragraph",
        version: 1,
      },
    ],
    direction: "ltr",
    format: "",
    indent: 0,
    type: "root",
    version: 1,
  },
} as unknown as SerializedEditorState;

export function ConfigTab({ runCommand }: ConfigTabProps) {
  const [editorState, setEditorState] =
    useState<SerializedEditorState>(initialValue)

  return (
    <div>
      <Editor
        editorSerializedState={editorState}
        onSerializedChange={(value) => setEditorState(value)}
      />
    </div>
  )
}