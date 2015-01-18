varying vec4 diffuse,ambient;
varying vec3 normal,halfVector;
varying vec4 position;
uniform sampler2D tex0;

void main()
{
	vec3 n = normalize(normal);
	float NdotL = max(dot(n,gl_LightSource[0].position.xyz),0.0);
	vec4 color = ambient;
	
	if (NdotL > 0.0) {
		color += diffuse * NdotL;
		color += gl_FrontMaterial.specular * gl_LightSource[0].specular * pow(max(dot(n,normalize(halfVector)),0.0), gl_FrontMaterial.shininess);
	}
	
	color *= texture2D(tex0, gl_TexCoord[0].st);
	
	gl_FragColor = mix(vec4(0.8, 0.8, 0.8, 1.0), color, clamp(exp(-pow(0.01*length(position), 2.0)), 0.0, 1.0));
}
