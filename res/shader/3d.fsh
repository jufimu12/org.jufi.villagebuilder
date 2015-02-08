varying vec4 diffuse,ambient,gambient;
varying vec3 normal,halfVector;
varying vec4 position,ecPos;
uniform sampler2D tex0;

void main()
{
	vec4 color;
	vec3 n = normalize(normal);
	
	if(gl_LightSource[0].position.w == 0) {
		color = ambient;
		float NdotL = max(dot(n,normalize(gl_LightSource[0].position.xyz)),0.0);
		
		if (NdotL > 0.0) {
			color += diffuse * NdotL;
			color += gl_FrontMaterial.specular * gl_LightSource[0].specular * pow(max(dot(n,normalize(halfVector)),0.0), gl_FrontMaterial.shininess);
		}
	} else {
		color = gambient;
		vec3 lightDir = vec3(gl_LightSource[0].position-ecPos);
		float dist = length(lightDir);
		float NdotL = max(dot(n,normalize(lightDir)),0.0);
		
		if (NdotL > 0.0) {
			float att = 1.0 / (gl_LightSource[0].constantAttenuation + 
				gl_LightSource[0].linearAttenuation * dist + 
				gl_LightSource[0].quadraticAttenuation * dist * dist);
			color += att * (diffuse * NdotL + ambient);
			
			vec3 halfV = normalize(halfVector);
			float NdotHV = max(dot(n,halfV),0.0);
			color += att * gl_FrontMaterial.specular * gl_LightSource[0].specular * pow(NdotHV,gl_FrontMaterial.shininess);
		}
	}
	
	color *= texture2D(tex0, gl_TexCoord[0].st);
	gl_FragColor = mix(vec4(0.8, 0.8, 0.8, 1.0), color, clamp(exp(-pow(0.01*length(position), 2.0)), 0.0, 1.0));
}
