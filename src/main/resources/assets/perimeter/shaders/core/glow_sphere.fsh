#version 150

in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // Эффект свечения с плавным затуханием к краям
    float glow = smoothstep(0.5, 0.8, 1.0 - length(gl_PointCoord - 0.5));
    fragColor = vertexColor * glow;
    fragColor.rgb *= fragColor.a;// Умножение на альфа для аддитивного эффекта
}