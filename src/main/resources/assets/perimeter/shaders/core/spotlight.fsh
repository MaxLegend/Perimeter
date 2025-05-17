#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec3 worldNormal;

out vec4 fragColor;

void main() {
    // 1) радиальный отступ (расстояние от оси Z)
    float radial = length(worldNormal.xy);
    // гауссово затухание: k — «ширина» пучка, чем больше, тем круче кромка
    float radialAtt = exp(-radial * radial * 6.0);

    // 2) осевой отступ (от вершины к основанию)
    // worldNormal.z от -1 (вершина) до +1 (основание)
    float axial = worldNormal.z * 0.1 + 0.5;
    // плавное экспоненциальное увеличение размытия к основанию
    float axialAtt = exp(-axial * axial * 7.0);

    // 3) комбинированный коэффициент видимости
    float visibility = radialAtt * axialAtt;

    vec4 color = vertexColor * ColorModulator;

    // 4) осветление: чуть посильнее ближе к оси, мягкое затухание к краю
    color.a *= visibility * 0.3;// base alpha очень малая
    color.rgb *= 1.0 + (1.0 - visibility) * 2.0;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}